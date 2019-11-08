package ru.viscur.dh.queue.impl.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.RECALC_NEXT_OFFICE_CONFIG_CODE
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.code
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
        deleteFromOfficeQueue(patientId)//на случай пересоздания маршрутного листа
        addToOfficeQueue(patientId, serviceRequests.first().locationReference?.first()?.id
                ?: throw Exception("not defined location for service request with id '${serviceRequests.first().id}'"))
        return serviceRequests
    }

    @Tx
    override fun calcServiceRequestExecOrders(patientId: String, prevOfficeId: String?): List<ServiceRequest> {
        return serviceRequestsExecutionCalculator.calcServiceRequestExecOrders(patientId, prevOfficeId)
    }

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

    /**
     * Проверить вход в кабинет: если есть возможность запускаем первого в очереди
     */
    private fun checkEntryToOffice(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status == LocationStatus.READY) {
            sendFirstToSurvey(officeId)
        }
    }

    /**
     * Отправить первого в очереди в кабинет
     */
    private fun sendFirstToSurvey(officeId: String) {
        val patientId = officeService.firstPatientIdInQueue(officeId)
        patientId?.run {
            officeService.changeStatus(officeId, LocationStatus.WAITING_PATIENT)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.GOING_TO_OBSERVATION, officeId)
            //todo уведомить о необходимоси пройти в кабинет
        }
    }

    /**
     * id следующего кабинета: непройденное обследование в маршрутном листе пациента с минимальным [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    private fun nextOfficeId(patientId: String, prevOfficeId: String?): String? =
            if (needRecalcNextOffice()) serviceRequestsExecutionCalculator.calcNextOfficeId(patientId, prevOfficeId)
            else serviceRequestService.active(patientId).firstOrNull()?.locationReference?.first()?.id

    @Tx
    override fun forceSendPatientToObservation(patientId: String, officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            deleteFromOfficeQueue(patientId)
            officeService.addPatientToQueue(officeId, patientId, estDuration(officeId, patientId), asFirst = true)
            officeService.changeStatus(officeId, LocationStatus.WAITING_PATIENT)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.GOING_TO_OBSERVATION, officeId)
        }
    }

    @Tx
    override fun deleteFromOfficeQueue(patientId: String) {
        val patient = patientService.byId(patientId)
        val patientQueueStatus = patient.extension.queueStatus
        //пациент стоит в очереди
        if (patientQueueStatus != PatientQueueStatus.READY) {
            //пациент в очереди (ожидает, идет на обслед. или на обслед.) - необходимо удалить из очереди, освободить если нужно кабинет
            val officeId = queueService.isPatientInOfficeQueue(patientId)
                    ?: throw Exception("Patient has queue status $patientQueueStatus but he is not in any QueueItem")
            //его ожидают в кабинете или идет осмотр - освободим кабинет
            if (patientQueueStatus in listOf(PatientQueueStatus.GOING_TO_OBSERVATION, PatientQueueStatus.ON_OBSERVATION)) {
                officeService.changeStatus(officeId, LocationStatus.BUSY)
                officeService.deleteFirstPatientFromQueue(officeId)
            } else {
                //пациент просто в очереди. его очередь не настала. просто удаляем из очереди
                officeService.deletePatientFromQueue(officeId, patientId)
            }
            //если прервано обследование, то не записываем в историю продолжительность
            val saveCurrentStatusToHistory = patientQueueStatus != PatientQueueStatus.ON_OBSERVATION
            patientStatusService.changeStatus(patientId, PatientQueueStatus.READY, officeId, saveCurrentStatusToHistory)
        }
        officeService.deletePatientFromLastPatientInfo(patientId)
    }

    @Tx
    override fun patientEntered(patientId: String, officeId: String): List<ServiceRequest> {
        if (officeService.firstPatientIdInQueue(officeId) == patientId) {
            val patient = patientService.byId(patientId)
            if (patient.extension.queueStatus == PatientQueueStatus.GOING_TO_OBSERVATION) {
                officeService.changeStatus(officeId, LocationStatus.OBSERVATION, patientId)
                patientStatusService.changeStatus(patientId, PatientQueueStatus.ON_OBSERVATION, officeId)
                return serviceRequestService.active(patientId, officeId)
            }
        }
        return listOf()
    }

    @Tx
    override fun patientLeft(officeId: String) {
        val patientId = officeService.firstPatientIdInQueue(officeId)!!
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus == PatientQueueStatus.ON_OBSERVATION) {
            officeService.changeStatus(officeId, LocationStatus.BUSY, patientId)
            officeService.deleteFirstPatientFromQueue(officeId)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.READY, officeId)
            addToQueue(patientId, officeId)
            officeService.updateLastPatientInfo(officeId, patientId, queueService.isPatientInOfficeQueue(patientId))
        }
    }

    @Tx
    override fun patientLeftByPatientId(patientId: String) {
        queueService.isPatientInOfficeQueue(patientId)?.run {
            patientLeft(this)
        }
    }

    @Tx
    override fun cancelEntering(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            val patientId = officeService.firstPatientIdInQueue(officeId)!!
            officeService.changeStatus(officeId, LocationStatus.BUSY, patientId)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE, officeId, saveCurrentStatusToHistory = false)
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
    override fun deleteQueue() {
        queueService.involvedOffices().forEach {
            officeService.changeStatus(it.id, LocationStatus.BUSY)
        }
        queueService.involvedPatients().forEach {
            patientStatusService.changeStatus(it.id, PatientQueueStatus.READY)
        }
        resourceService.deleteAll(ResourceType.QueueItem)
    }

    override fun deleteHistory() {
        resourceService.deleteAll(ResourceType.QueueHistoryOfOffice)
        resourceService.deleteAll(ResourceType.QueueHistoryOfPatient)
    }

    override fun queueOfOffice(officeId: String): Bundle =
            Bundle(entry = queueService.queueItemsOfOffice(officeId).map { BundleEntry(it) })

    override fun queueItems(): List<QueueItem> = queueService.queueItems()

    override fun loqAndValidate(): String {
        val offices = officeService.all()
        val str = mutableListOf<String>()
        offices.forEach { office ->
            str.add("queue for ${office.id} (${office.status}):")

            val queue = queueService.queueItemsOfOffice(office.id)
            queue.forEach { queueItem ->
                str.add("$queueItem")
            }
            office.extension?.lastPatientInfo?.run {
                str.add("  lastPatientInfo: " + (this.subject.reference) + ", " +
                        (this.nextOffice?.reference ?: ""))
            }

            //при пустой очереди кабинет не должен иметь назначенного пациента
            if (queue.isEmpty()) {
                if (office.status in listOf(LocationStatus.WAITING_PATIENT, LocationStatus.OBSERVATION)) {
                    str.add("ERROR. office has wrong status of queue (${office.status}) with empty queue")
                }
                return@forEach
            }

            val firstPatientId = officeService.firstPatientIdInQueue(office.id)
            val firstPatient = patientService.byId(firstPatientId!!)
            //статус первого в очереди
            if (firstPatient.extension.queueStatus !in listOf(PatientQueueStatus.ON_OBSERVATION, PatientQueueStatus.GOING_TO_OBSERVATION, PatientQueueStatus.IN_QUEUE)) {
                str.add("ERROR. first patient in queue to office ${office.id} has wrong status ${firstPatient.extension.queueStatus}")
            }
            //статусы всех кроме первого в очереди
            if (queue.filterIndexed { i, _ -> i > 0 }.any { it.patientQueueStatus != PatientQueueStatus.IN_QUEUE }) {
                str.add("ERROR. all patients except first must have status IN_QUEUE. Office with error: ${office.id}")
            }
            //один пациент не может стоять в очереди несколько раз в один офис
            queue.groupBy { it.subject.id }.forEach { (patientId, queueItems) ->
                if (queueItems.size > 1) {
                    str.add("ERROR. patient with id $patientId is in several queue items to ${office.id}")
                }
            }
            //совпадение статуса кабинета и статуса первого в очереди
            if (office.status == LocationStatus.WAITING_PATIENT) {
                if (firstPatient.extension.queueStatus != PatientQueueStatus.GOING_TO_OBSERVATION) {
                    str.add("ERROR. ${office.id} is waiting patient but first patient has wrong status: $firstPatient")
                }
            } else if (office.status == LocationStatus.OBSERVATION) {
                if (firstPatient.extension.queueStatus != PatientQueueStatus.ON_OBSERVATION) {
                    str.add("ERROR. ${office.id} has survey status but first patient has wrong status: $firstPatient")
                }
            } else if (firstPatient.extension.queueStatus != PatientQueueStatus.IN_QUEUE) {
                str.add("ERROR. ${office.id} has one of inactive status but first patient has wrong status: $firstPatient")
            }
            //Порядок очереди: кр, ж, з
            val lastRed = queue.indexOfLast { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.RED }
            val firstYellow = queue.indexOfFirst { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.YELLOW }
            val lastYellow = queue.indexOfLast { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.YELLOW }
            val firstGreen = queue.indexOfFirst { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE && it.severity == Severity.GREEN }
            if (firstYellow != -1 && lastRed != -1 && lastRed > firstYellow) {
                str.add("ERROR. in ${office.id} index of yellow severity patient ($firstYellow) is less than index of red severity patient ($lastRed)")
            }
            if (firstGreen != -1 && lastYellow != -1 && lastYellow > firstGreen) {
                str.add("ERROR. in ${office.id} index of green severity patient ($firstYellow) is less than index of yellow severity patient ($lastYellow)")
            }
        }
        //один пациент не может стоять в несколько очередей в разные кабинеты
        val allQueueItems = resourceService.all(ResourceType.QueueItem, RequestBodyForResources(filter = mapOf()))
        allQueueItems.groupBy { it.subject.id }.forEach { (patientId, queueItems) ->
            if (queueItems.size > 1) {
                str.add("ERROR. patient with id $patientId is in several office queues")
            }
        }
        //один пациент не должен отображаться в информации о посл пациенте в неск кабинетах
        offices.mapNotNull { it.extension?.lastPatientInfo }.groupBy { it.subject.id }.forEach { (patientId, offices) ->
            if (offices.size > 1) {
                str.add("ERROR. patient with id $patientId is in several lastPatientInfo $offices")
            }
        }
        log.info("\n${str.joinToString("\n")}")
        return str.joinToString("\n<br/>")
    }

    /**
     * Добавление пациента в очередь в указанный кабинет
     */
    private fun addToOfficeQueue(patientId: String, officeId: String) {
        officeService.addPatientToQueue(officeId, patientId, estDuration(officeId, patientId))
        patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE)
        checkEntryToOffice(officeId)
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
}