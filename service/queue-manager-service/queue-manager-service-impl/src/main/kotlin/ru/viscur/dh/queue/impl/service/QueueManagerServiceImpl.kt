package ru.viscur.dh.queue.impl.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.RECALC_NEXT_OFFICE_CONFIG_CODE
import ru.viscur.dh.fhir.model.dto.LocationMonitorDto
import ru.viscur.dh.fhir.model.dto.LocationMonitorNextOfficeForPatientInfoDto
import ru.viscur.dh.fhir.model.dto.LocationMonitorQueueItemDto
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.criticalTimeForDelayGoingToObservation
import ru.viscur.dh.fhir.model.utils.criticalTimeForDeletingNextOfficeForPatientsInfo
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.PatientStatusService
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.transaction.desc.config.annotation.Tx

@Service
class QueueManagerServiceImpl(
        private val officeService: OfficeService,
        private val patientStatusService: PatientStatusService,
        private val patientService: PatientService,
        private val locationService: LocationService,
        private val queueService: QueueService,
        private val resourceService: ResourceService,
        private val serviceRequestService: ServiceRequestService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val serviceRequestsExecutionCalculator: ServiceRequestsExecutionCalculator,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val configService: ConfigService
) : QueueManagerService {

    companion object {
        private val log = LoggerFactory.getLogger(QueueManagerServiceImpl::class.java)
    }

    override fun needRecalcNextOffice() = configService.readBool(RECALC_NEXT_OFFICE_CONFIG_CODE)

    @Tx
    override fun recalcNextOffice(value: Boolean) {
        configService.write(RECALC_NEXT_OFFICE_CONFIG_CODE, value.toString())
    }

    @Tx
    override fun registerPatient(patientId: String): List<ServiceRequest> {
        val serviceRequests = calcServiceRequestExecOrders(patientId)
        deleteFromQueue(patientId)//на случай пересоздания маршрутного листа
        addToOfficeQueue(patientId, serviceRequests.first().locationReference?.first()?.id
                ?: throw Exception("not defined location for service request with id '${serviceRequests.first().id}'"))
        return serviceRequests
    }

    @Tx
    override fun calcServiceRequestExecOrders(patientId: String, prevOfficeId: String?): List<ServiceRequest> =
            serviceRequestsExecutionCalculator.calcServiceRequestExecOrders(patientId, prevOfficeId)

    @Tx
    override fun addToQueue(patientId: String, prevOfficeId: String?) {
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus != PatientQueueStatus.READY) {
            return
        }
        val nextOfficeId = nextOfficeId(patientId, prevOfficeId)
        nextOfficeId?.run {
            addToOfficeQueue(patientId, nextOfficeId)
        }
    }

    @Tx
    override fun addToOfficeQueue(patientId: String, officeId: String) {
        officeService.addPatientToQueue(officeId, patientId, estDuration(officeId, patientId))
        patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE)
        checkEntryToOffice(officeId)
    }

    @Tx
    override fun forceSendPatientToObservation(patientId: String, officeId: String): List<ServiceRequest> {
        setAsFirst(patientId, officeId)
        sendFirstToObservation(officeId)
        return activeServiceRequestInOffice(patientId, officeId)
    }

    @Tx
    override fun setAsFirst(patientId: String, officeId: String) {
        val prevOfficeId = queueService.isPatientInOfficeQueue(patientId)
        deleteFromQueue(patientId)
        officeService.addPatientToQueue(officeId, patientId, estDuration(officeId, patientId), toIndex = 0)
        patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE)
        checkEntryToOffice(officeId)
        if (prevOfficeId != null && prevOfficeId != officeId) {
            officeService.addToNextOfficeForPatientsInfo(prevOfficeId, patientId, officeId)
        }
    }

    @Tx
    override fun delayGoingToObservation(patientId: String, onlyIfFirstInQueueIsLongWaiting: Boolean) {
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus == PatientQueueStatus.GOING_TO_OBSERVATION) {
            val officeId = queueService.isPatientInOfficeQueue(patientId) ?: return
            val firstPatientIdInQueue = officeService.firstPatientIdInQueue(officeId)
            firstPatientIdInQueue?.run {
                if (!onlyIfFirstInQueueIsLongWaiting || patientService.byId(firstPatientIdInQueue).extension.queueStatusUpdatedAt?.before(criticalTimeForDelayGoingToObservation()) != false) {
                    deleteFromQueue(patientId)
                    officeService.addPatientToQueue(officeId, patientId, estDuration(officeId, patientId), toIndex = 1)
                    patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE)
                    enterNextPatient(officeId)
                }
            }
        }
    }

    @Tx
    override fun rebasePatientIfNeeded(patientId: String, officeId: String) {
        val currentOfficeId = queueService.isPatientInOfficeQueue(patientId)
        if (currentOfficeId == officeId && serviceRequestService.active(patientId, officeId).isEmpty()) {
            deleteFromQueue(patientId)
            addToQueue(patientId)
            val nextOfficeId = queueService.isPatientInOfficeQueue(patientId)
            nextOfficeId?.run { officeService.addToNextOfficeForPatientsInfo(officeId, patientId, nextOfficeId) }
        }
//         todo не понятно. вроде пересчитывать даже если идет обсл-е
//        if (currentOfficeId == officeId) {
//            val patient = patientService.byId(patientId)
//            if (patient.extension.queueStatus == PatientQueueStatus.IN_QUEUE && serviceRequestService.active(patientId, officeId).isEmpty()) {
//                deleteFromQueue(patientId)
//                addToQueue(patientId)
//            }
//        }
    }

    @Tx
    override fun deleteFromQueue(patientId: String) {
        val patient = patientService.byId(patientId)
        val patientQueueStatus = patient.extension.queueStatus
        //пациент стоит в очереди
        if (patientQueueStatus != PatientQueueStatus.READY) {
            //пациент в очереди (ожидает, идет на обслед. или на обслед.) - необходимо удалить из очереди, освободить если нужно кабинет
            val officeId = queueService.isPatientInOfficeQueue(patientId)
                    ?: throw Exception("Patient with id '$patientId' has queue status $patientQueueStatus but he is not in any QueueItem")
            officeService.deletePatientFromQueue(officeId, patientId)
            //если прервано обследование, то не записываем в историю продолжительность
            val saveCurrentStatusToHistory = patientQueueStatus != PatientQueueStatus.ON_OBSERVATION
            patientStatusService.changeStatus(patientId, PatientQueueStatus.READY, officeId, saveCurrentStatusToHistory)
            changeOfficeStatusNotReadyToProper(officeId)
        }
        officeService.deletePatientFromNextOfficesForPatientsInfo(patientId)
    }

    @Tx
    override fun patientEntered(patientId: String, officeId: String): List<ServiceRequest> {
        if (queueService.isPatientInOfficeQueue(patientId) == officeId) {
            val patient = patientService.byId(patientId)
            if (patient.extension.queueStatus == PatientQueueStatus.GOING_TO_OBSERVATION) {
                officeService.changeStatus(officeId, LocationStatus.OBSERVATION)
                patientStatusService.changeStatus(patientId, PatientQueueStatus.ON_OBSERVATION, officeId)
                officeService.deletePatientFromNextOfficesForPatientsInfo(patientId)
                return activeServiceRequestInOffice(patientId, officeId)
            }
        }
        return listOf()
    }

    @Tx
    override fun patientLeft(patientId: String, officeId: String) {
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus == PatientQueueStatus.ON_OBSERVATION) {
            officeService.deletePatientFromQueue(officeId, patientId)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.READY, officeId)
            changeOfficeStatusNotReadyToProper(officeId)
            addToQueue(patientId = patientId, prevOfficeId = officeId)
            val nextOfficeId = queueService.isPatientInOfficeQueue(patientId)
            nextOfficeId?.run { officeService.addToNextOfficeForPatientsInfo(officeId, patientId, nextOfficeId) }
        }
    }

    @Tx
    override fun patientLeftByPatientId(patientId: String) {
        queueService.isPatientInOfficeQueue(patientId)?.run {
            patientLeft(patientId, this)
        }
    }

    @Tx
    override fun cancelEntering(patientId: String) {
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus in listOf(PatientQueueStatus.GOING_TO_OBSERVATION, PatientQueueStatus.ON_OBSERVATION)) {
            val officeId = queueService.isPatientInOfficeQueue(patientId)
            setAsFirst(patientId, officeId!!)
        }
    }

    @Tx
    override fun severityUpdated(patientId: String, severity: Severity) {
        serviceRequestsExecutionCalculator.recalcOfficeForInspectionOfResp(patientId, severity)
        val officeId = queueService.isPatientInOfficeQueue(patientId)
        officeId?.run {
            deleteFromQueue(patientId)
            addToOfficeQueue(patientId, officeId)
        }
    }

    @Tx
    override fun officeIsReady(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            officeService.changeStatus(officeId, LocationStatus.READY)
            checkEntryToOffice(officeId)
        }
    }

    @Tx
    override fun enterNextPatient(officeId: String) {
        sendFirstToObservation(officeId)
    }

    @Tx
    override fun officeIsBusy(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            officeService.changeStatus(officeId, LocationStatus.BUSY)
        }
    }

    @Tx
    override fun officeIsClosed(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            officeService.changeStatus(officeId, LocationStatus.CLOSED)
            val queue = queueService.queueItemsOfOffice(officeId)
            queue.forEach {
                val patientId = it.subject.id
                patientStatusService.changeStatus(patientId!!, PatientQueueStatus.READY)
                addToQueue(patientId, officeId)
            }
            queueService.deleteQueueItemsOfOffice(officeId)
        }
    }

    @Tx
    override fun deleteOldNextOfficeForPatientsInfo() {
        locationService.withOldNextOfficeForPatientsInfo().forEach {
            resourceService.update(ResourceType.Location, it.id) {
                extension.nextOfficeForPatientsInfo =
                        extension.nextOfficeForPatientsInfo.filter { it.fireDate.after(criticalTimeForDeletingNextOfficeForPatientsInfo()) }
            }
        }
    }

    @Tx
    override fun deleteQueue() {
        queueService.involvedOffices().forEach {
            officeService.changeStatus(it.id, LocationStatus.BUSY)
        }
        queueService.involvedPatients().forEach {
            patientStatusService.changeStatus(it.id, PatientQueueStatus.READY)
        }
        resourceService.all(ResourceType.Location, RequestBodyForResources(mapOf())).forEach {
            resourceService.update(ResourceType.Location, it.id) {
                extension.nextOfficeForPatientsInfo = listOf()
            }
        }
        resourceService.deleteAll(ResourceType.QueueItem)
    }

    @Tx
    override fun deleteHistory() {
        resourceService.deleteAll(ResourceType.QueueHistoryOfOffice)
        resourceService.deleteAll(ResourceType.QueueHistoryOfPatient)
    }

    override fun queueOfOffice(officeId: String): Bundle =
            Bundle(entry = queueService.queueItemsOfOffice(officeId).map { BundleEntry(it) })

    override fun queueItems(): List<QueueItem> = queueService.queueItems()

    override fun locationMonitor(officeId: String): LocationMonitorDto {
        val office = locationService.byId(officeId)
        clinicalImpressionService
        return LocationMonitorDto(
                officeId = officeId,
                officeStatus = office.status.name,
                locationType = office.type(),
                items = queueService.queueItemsOfOffice(officeId).map { queueItem ->
                    LocationMonitorQueueItemDto(
                            onum = queueItem.onum!!,
                            patientId = queueItem.subject.id!!,
                            status = queueItem.patientQueueStatus!!.name,
                            severity = queueItem.severity!!.name,
                            queueNumber = queueItem.queueNumber
                    )
                },
                nextOfficeForPatientsInfo = office.extension.nextOfficeForPatientsInfo.map { nextOfficeForPatientInfo ->
                    LocationMonitorNextOfficeForPatientInfoDto(
                            patientId = nextOfficeForPatientInfo.subject.id!!,
                            severity = nextOfficeForPatientInfo.severity.name,
                            queueNumber = nextOfficeForPatientInfo.queueNumber,
                            nextOfficeId = nextOfficeForPatientInfo.nextOffice.id!!
                    )
                }
        )
    }

    override fun loqAndValidate(): String {
        val offices = officeService.all()
        val str = mutableListOf<String>()
        offices.forEach { office ->
            str.add("queue for ${office.id} (${office.status}):")

            val queue = queueService.queueItemsOfOffice(office.id)
            queue.forEach { queueItem ->
                str.add("$queueItem")
            }
            if (office.extension.nextOfficeForPatientsInfo.isNotEmpty()) {
                str.add("  lastPatientInfo:")
                office.extension.nextOfficeForPatientsInfo.forEach {
                    str.add("    " + it.subject.id + " (${it.severity}) to " + it.nextOffice.id)
                }
            }

            //при пустой очереди кабинет не должен иметь назначенного пациента
            if (queue.isEmpty()) {
                if (office.status in listOf(LocationStatus.WAITING_PATIENT, LocationStatus.OBSERVATION)) {
                    str.add("ERROR. office has wrong status of queue (${office.status}) with empty queue")
                }
                return@forEach
            }

            val firstPatientId = queue.first().subject.id
            val firstPatient = patientService.byId(firstPatientId!!)
            //статус первого в очереди
            if (firstPatient.extension.queueStatus !in listOf(PatientQueueStatus.ON_OBSERVATION, PatientQueueStatus.GOING_TO_OBSERVATION, PatientQueueStatus.IN_QUEUE)) {
                str.add("ERROR. first patient in queue to office ${office.id} has wrong status ${firstPatient.extension.queueStatus}")
            }
            //статусы всех кроме первых (на обсл. или проходящих на обсл.) в очереди
            //это первый со статусом IN_QUEUE
            val firstPatientInQueueId = officeService.firstPatientIdInQueue(office.id)
            firstPatientInQueueId?.run {
                val firstPatientInQueueIndex = queue.find { it.subject.id!! == firstPatientInQueueId }?.onum!!
                if (queue.filterIndexed { i, _ -> i >= firstPatientInQueueIndex }.any { it.patientQueueStatus != PatientQueueStatus.IN_QUEUE }) {
                    str.add("ERROR. all patients except several first patients must have status IN_QUEUE. Office with error: ${office.id}")
                }
            }
            //один пациент не может стоять в очереди несколько раз в один офис
            queue.groupBy { it.subject.id }.forEach { (patientId, queueItems) ->
                if (queueItems.size > 1) {
                    str.add("ERROR. patient with id $patientId is in several queue items to ${office.id}")
                }
            }
            //совпадение статуса кабинета и статусов пациентов в очереди
            val expOfficeStatus =
                    when {
                        queue.any { it.patientQueueStatus == PatientQueueStatus.ON_OBSERVATION } -> LocationStatus.OBSERVATION
                        queue.any { it.patientQueueStatus == PatientQueueStatus.GOING_TO_OBSERVATION } -> LocationStatus.WAITING_PATIENT
                        else -> LocationStatus.BUSY
                    }
            if (office.status != expOfficeStatus) {
                str.add("ERROR. ${office.id} is has wrong status. exp: $expOfficeStatus, actual: ${office.status}")
            }
//            //Порядок очереди: кр, ж, з
//            val lastRed = queue.indexOfLast { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.RED }
//            val firstYellow = queue.indexOfFirst { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.YELLOW }
//            val lastYellow = queue.indexOfLast { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.YELLOW }
//            val firstGreen = queue.indexOfFirst { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.GREEN }
//            if (firstYellow != -1 && lastRed != -1 && lastRed > firstYellow) {
//                str.add("ERROR. in ${office.id} index of yellow severity patient ($firstYellow) is less than index of red severity patient ($lastRed)")
//            }
//            if (firstGreen != -1 && lastYellow != -1 && lastYellow > firstGreen) {
//                str.add("ERROR. in ${office.id} index of green severity patient ($firstYellow) is less than index of yellow severity patient ($lastYellow)")
//            }
        }
        //один пациент не может стоять в несколько очередей в разные кабинеты
        val allQueueItems = resourceService.all(ResourceType.QueueItem, RequestBodyForResources(filter = mapOf()))
        allQueueItems.groupBy { it.subject.id }.forEach { (patientId, queueItems) ->
            if (queueItems.size > 1) {
                str.add("ERROR. patient with id $patientId is in several office queues")
            }
        }
        //один пациент не должен отображаться в информации о посл пациенте в неск кабинетах
        offices.map { it.extension.nextOfficeForPatientsInfo }.flatten().groupBy { it.subject.id }.forEach { (patientId, offices) ->
            if (offices.size > 1) {
                str.add("ERROR. patient with id $patientId is in several nextOfficeForPatientsInfo $offices")
            }
        }
        log.info("\n${str.joinToString("\n")}")
        return str.joinToString("\n<br/>")
    }

    /**
     * Предположительная продолжительность осмотра в кабинете:
     * сумма предп. продолжительностей всех непройденных услуг из маршрутного листа, которые м б проведены в этом кабинете
     */
    private fun estDuration(officeId: String, patientId: String): Int {
        val serviceRequests = serviceRequestService.active(patientId, officeId)
        if (serviceRequests.isEmpty()) return 0
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        diagnosis?.run {
            return serviceRequests.sumBy {
                observationDurationService.estimate(it.code.code(), diagnosis, patientService.severity(patientId))
            }
        }
        return 0
    }

    private fun changeOfficeStatusNotReadyToProper(officeId: String) {
        val queueItems = queueService.queueItems()
        val officeStatus =
                when {
                    queueItems.any { it.patientQueueStatus == PatientQueueStatus.ON_OBSERVATION } -> LocationStatus.OBSERVATION
                    queueItems.any { it.patientQueueStatus == PatientQueueStatus.GOING_TO_OBSERVATION } -> LocationStatus.WAITING_PATIENT
                    else -> LocationStatus.BUSY
                }
        officeService.changeStatus(officeId, officeStatus)
    }

    /**
     * Проверить вход в кабинет: если есть возможность запускаем первого в очереди
     */
    private fun checkEntryToOffice(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status == LocationStatus.READY) {
            sendFirstToObservation(officeId)
        }
    }

    /**
     * Отправить первого в очереди в кабинет на обследования
     */
    private fun sendFirstToObservation(officeId: String) {
        val patientId = officeService.firstPatientIdInQueue(officeId)
        patientId?.run {
            patientStatusService.changeStatus(patientId, PatientQueueStatus.GOING_TO_OBSERVATION, officeId)
            changeOfficeStatusNotReadyToProper(officeId)
            //todo уведомить о необходимоси пройти в кабинет
        }
    }

    /**
     * id следующего кабинета:
     * если выключена настройка [needRecalcNextOffice]: непройденное обследование в маршрутном листе пациента с минимальным [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     * если включена то оптимальный след. кабинет учитывая приоритет, очереди, дальность и т.д.
     */
    private fun nextOfficeId(patientId: String, prevOfficeId: String?): String? =
            if (needRecalcNextOffice()) serviceRequestsExecutionCalculator.calcNextOfficeId(patientId, prevOfficeId)
            else serviceRequestService.active(patientId).firstOrNull()?.locationReference?.first()?.id

    /**
     * Непройденные назначения, которые могут быть пройдены в этом кабинете
     * Если таковых нет, то все непройденные назначения
     */
    private fun activeServiceRequestInOffice(patientId: String, officeId: String): List<ServiceRequest> {
        var serviceRequests = serviceRequestService.active(patientId, officeId)
        if (serviceRequests.isEmpty()) {
            serviceRequests = serviceRequestService.active(patientId)
        }
        return serviceRequests
    }
}