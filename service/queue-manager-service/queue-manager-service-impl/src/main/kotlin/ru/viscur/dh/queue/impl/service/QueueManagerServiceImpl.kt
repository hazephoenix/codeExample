package ru.viscur.dh.queue.impl.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.PatientStatusService
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.queue.api.model.Office
import ru.viscur.dh.queue.api.model.RouteSheet
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY

@Service
class QueueManagerServiceImpl(
        private val officeService: OfficeService,
        private val patientStatusService: PatientStatusService,
        private val patientService: PatientService,
        private val conceptService: ConceptService,
        private val locationService: LocationService,
        private val queueService: QueueService,
        private val resourceService: ResourceService
) : QueueManagerService {

    companion object {
        private val log = LoggerFactory.getLogger(QueueManagerServiceImpl::class.java)
    }

    override fun registerPatient(patientId: String): List<ServiceRequest> {
        val serviceRequests = patientService.serviceRequests(patientId)
                .sortedWith(
                        compareBy({
                            -priority(it)
                        }, {
                            -estWaitingInQueueWithType(patientId, it)
                        })
                )
        serviceRequests.forEachIndexed { index, serviceRequest ->
            serviceRequest.extension = serviceRequest.extension?.apply { executionOrder = index }
                    ?: ServiceRequestExtension(executionOrder = index)
            resourceService.update(serviceRequest)
        }
        addToOfficeQueue(patientId)
        return serviceRequests
    }

    private fun estDuration(patientId: String, officeId: String): Int {
        //todo это диагноз пациента + тип пациента + типы процедур в этом кабинете -> продолжительность обслуживания. зашитое сопоставление
        return 1000
    }

    /**
     * Предположительное время ожидания в очереди пациента с опр. степенью тяжести
     * Сумма приблизительных продолжительностей осмотра всех пациентов перед позицией в очереди, куда бы встал пациент с своей степенью тяжести
     */
    private fun estWaitingInQueueWithType(patientId: String, serviceRequest: ServiceRequest): Int {
        val officeId = serviceRequest.locationReference!!.first().id
        val queue = queueService.queueItemsOfOffice(officeId!!)
        val inQueue = queue.filter { it.patientQueueStatus != PatientQueueStatus.ON_SURVEY }
        val severity = patientService.severity(patientId)
        val inQueueByType = when (severity) {
            Severity.RED -> inQueue.filter { it.severity == severity }
            Severity.YELLOW -> inQueue.filter { it.severity in SEVERITY_WITH_PRIORITY }
            else -> inQueue
        }
        return inQueueByType.sumBy { it.estDuration }
    }

    /**
     * Приоритет у услуги
     */
    private fun priority(serviceRequest: ServiceRequest): Double {
        //осмотр ответсвенного в посл очередь
        if (!serviceRequest.performer.isNullOrEmpty()) {
            return 0.0
        }
        val observationType = conceptService.byCodeableConcept(serviceRequest.code)
        observationType.parentCode ?: throw Exception("Observation type ${serviceRequest.code} has no parentCode")
        val observationCategory = conceptService.parent(observationType)!!
        return observationCategory.priority ?: 0.5
    }

    override fun addToOfficeQueue(patientId: String) {
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus !in listOf(PatientQueueStatus.FINISHED, PatientQueueStatus.READY)) {
            return
        }
        val nextOfficeId = nextOfficeId(patientId)
        nextOfficeId?.run {
            officeService.addPatientToQueue(patientId, nextOfficeId, estDuration(patientId, nextOfficeId))
            patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE)
            checkEntryToOffice(nextOfficeId)
        } ?: run {
            //завершил выполнение
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
        val patientId = officeService.firstPatientInQueue(officeId)
        patientId?.run {
            officeService.changeStatus(officeId, LocationStatus.WAITING_PATIENT)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.GOING_TO_SURVEY, officeId)
            //todo уведомить о необходимоси пройти в кабинет
        }
    }

    /**
     * id следующего кабинета: непройденное обследование в маршрутном листе пациента с минимальным [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    private fun nextOfficeId(patientId: String): String? =
            patientService.activeServiceRequests(patientId).firstOrNull()?.locationReference?.first()?.id

    override fun deleteFromOfficeQueue(patientId: String) {
        val patient = patientService.byId(patientId)
        val patientQueueStatus = patient.extension.queueStatus
        //пациент стоит в очереди
        if (patientQueueStatus !in listOf(PatientQueueStatus.FINISHED, PatientQueueStatus.READY)) {
            //пациент в очереди (ожидает, идет на обслед. или на обслед.) - необходимо удалить из очереди, освободить если нужно кабинет
            val officeId = queueService.isPatientInOfficeQueue(patientId)
                    ?: throw Exception("Patient has queue status $patientQueueStatus but he is not in any QueueItem")
            //его ожидают в кабинете или идет осмотр - освободим кабинет
            if (patientQueueStatus in listOf(PatientQueueStatus.GOING_TO_SURVEY, PatientQueueStatus.ON_SURVEY)) {
                officeService.changeStatus(officeId, LocationStatus.BUSY)
                officeService.deleteFirstPatientFromQueue(officeId)
                checkEntryToOffice(officeId)
            } else {
                //пациент просто в очереди. его очередь не настала. просто удаляем из очереди
                officeService.deletePatientFromQueue(officeId, patientId)
            }
            //если прервано обследование, то не записываем в историю продолжительность
            val saveCurrentStatusToHistory = patientQueueStatus != PatientQueueStatus.ON_SURVEY
            patientStatusService.changeStatus(patientId, PatientQueueStatus.READY, officeId, saveCurrentStatusToHistory)
        }
        officeService.deletePatientFromLastPatientInfo(patientId)
    }

    override fun patientEntered(patientId: String, officeId: String) {
        if (officeService.firstPatientInQueue(officeId) == patientId) {
            val patient = patientService.byId(patientId)
            if (patient.extension.queueStatus == PatientQueueStatus.GOING_TO_SURVEY) {
                officeService.changeStatus(officeId, LocationStatus.OBSERVATION, patientId)
                patientStatusService.changeStatus(patientId, PatientQueueStatus.ON_SURVEY, officeId)
//                queueService.deleteQueueItemsOfOffice(patientId)//todo оставлять в очереди или нет?..
            }
        }
    }

    override fun patientLeft(officeId: String) {
        val patientId = officeService.firstPatientInQueue(officeId)!!
        val patient = patientService.byId(patientId)
        if (patient.extension.queueStatus == PatientQueueStatus.ON_SURVEY) {
            officeService.changeStatus(officeId, LocationStatus.BUSY, patientId)
            officeService.deleteFirstPatientFromQueue(officeId)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.READY, officeId)
            addToOfficeQueue(patientId)
            officeService.updateLastPatientInfo(officeId, patientId, queueService.isPatientInOfficeQueue(patientId))
        }
    }

    override fun officeIsReady(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            officeService.changeStatus(officeId, LocationStatus.READY)
            checkEntryToOffice(officeId)
        }
    }

    override fun officeIsBusy(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            officeService.changeStatus(officeId, LocationStatus.BUSY)
        }
    }

    override fun officeIsClosed(officeId: String) {
        val office = locationService.byId(officeId)
        if (office.status !in listOf(LocationStatus.OBSERVATION, LocationStatus.WAITING_PATIENT)) {
            officeService.changeStatus(officeId, LocationStatus.CLOSED)
            val queue = queueService.queueItemsOfOffice(officeId)
            queue.forEach {
                val patientId = it.subject.id
                patientStatusService.changeStatus(patientId!!, PatientQueueStatus.READY)
                addToOfficeQueue(patientId)
            }
            queueService.deleteQueueItemsOfOffice(officeId)
        }
    }

    override fun deleteQueue() {
        println("deleteQueue")//todo
        queueService.involvedOffices().forEach {
            officeService.changeStatus(it.id!!, LocationStatus.BUSY)
        }
        queueService.involvedPatients().forEach {
            patientStatusService.changeStatus(it.id!!, PatientQueueStatus.READY)
        }
        resourceService.deleteAll(ResourceType.QueueItem)
    }

    override fun deleteHistory() {
        resourceService.deleteAll(ResourceType.QueueHistoryOfOffice)
        resourceService.deleteAll(ResourceType.QueueHistoryOfPatient)
    }

    private fun officesForSurveyType(surveyTypeId: Long): List<Office> {
        TODO("Not implemented");
    }


    /**
     * Кабинеты с наивысшим приоритетом посещения из непосещенных в маршрутном листе
     */
    private fun officesWithHighestPriority(routeSheet: RouteSheet): List<Office> {
        TODO("Restore");
        /* val notProcessedSurveys = routeSheet.surveys.filterNot { it.visited }
         val maxPriority = notProcessedSurveys.map { it.priority }.max()
         val surveysIdWithMaxPriority = notProcessedSurveys.filter { it.priority == maxPriority }.map { it.surveyType.id }
         return offices.filter { it.surveyType.id in surveysIdWithMaxPriority }*/
    }

    override fun loqAndValidate() {
        val offices = resourceService.all(ResourceType.Location, RequestBodyForResources(filter = mapOf(
                "id" to "Office:"
        )))
        println("loqAndValidate , ${offices.size}")//todo
        offices.forEach { office ->
            log.info("queue for ${office.id}:")

            val queue = queueService.queueItemsOfOffice(office.id!!)
            queue.forEach { queueItem ->
                log.info("$queueItem")
            }
            //при пустой очереди кабинет не должен иметь назначенного пациента

            if (queue.isEmpty() && office.status in listOf(LocationStatus.WAITING_PATIENT, LocationStatus.OBSERVATION)) {
                log.error("\n\nERROR. office has wrong status of queue ($office.status) with empty queue")
            }
        }
    }
}