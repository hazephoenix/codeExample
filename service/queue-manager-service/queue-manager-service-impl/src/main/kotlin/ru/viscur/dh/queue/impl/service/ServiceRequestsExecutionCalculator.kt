package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.RECEPTION
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY
import ru.viscur.dh.queue.impl.utils.DistanceBetweenOfficesCalculator

/**
 * Created at 07.11.2019 9:08 by SherbakovaMA
 *
 * Сервис определения порядка и местоположения прохождения назначений маршрутного листа
 */
@Service
class ServiceRequestsExecutionCalculator(
        private val patientService: PatientService,
        private val conceptService: ConceptService,
        private val locationService: LocationService,
        private val queueService: QueueService,
        private val resourceService: ResourceService,
        private val serviceRequestService: ServiceRequestService
) {

    /**
     * Проставление/перепроставление порядка у невыполненных назначений в маршрутном листе пациента
     */
    fun calcServiceRequestExecOrders(patientId: String, prevOfficeId: String?): List<ServiceRequest> {
        val serviceRequestExecInfos = serviceRequestInfos(patientId)
        val sortedServiceRequestExecInfos = calcLocationAndOrder(serviceRequestExecInfos, prevOfficeId)
        return sortedServiceRequestExecInfos.mapIndexed { index, info ->
            resourceService.update(ResourceType.ServiceRequest, info.serviceRequestId) {
                locationReference = listOf(referenceToLocation(info.locationId))
                extension = extension?.apply { executionOrder = index }
                        ?: ServiceRequestExtension(executionOrder = index)
            }
        }
    }

    /**
     * Получение информации о непройденных назначениях пациента [ServiceRequestExecInfo]:
     * id назначения, приоритет услуги,
     * список незакрытых кабинетов, где м б оказана процедура с указанием ожидания в очереди в каждый
     */
    private fun serviceRequestInfos(patientId: String): List<ServiceRequestExecInfo> {
        val serviceRequests = serviceRequestService.active(patientId)
        val severity = patientService.severity(patientId)
        return serviceRequests.map { serviceRequest ->
            val serviceRequestId = serviceRequest.id
            val locationInfos = locationService.byObservationType(serviceRequest.code.code()).map { officeId ->
                ServiceRequestExecLocationInfo(serviceRequestId, officeId, estWaitingInQueueWithType(officeId, severity))
            }
            if (locationInfos.isEmpty()) {
                throw Exception("ERROR. Can't find opened office by observation type '${serviceRequest.code.code()}'")
            }
            ServiceRequestExecInfo(serviceRequestId, priority(serviceRequest), locationInfos)
        }
    }

    fun calcNextOfficeId(patientId: String, prevOfficeId: String?): String? {
        val serviceRequestExecInfos = serviceRequestInfos(patientId)
        if (serviceRequestExecInfos.isEmpty()) return null
        val nextServiceRequestInfo = nextServiceRequest(serviceRequestExecInfos, prevOfficeId)
        return nextServiceRequestInfo.locationId
    }

    /**
     * Однозначно определяет в каком кабинете нужно провести назначение, упорядочивает список назначений
     */
    private fun calcLocationAndOrder(initServiceRequests: List<ServiceRequestExecInfo>, prevOfficeId: String?): List<ServiceRequestExecLocationInfo> {
        var initSr = initServiceRequests
        val result = mutableListOf<ServiceRequestExecLocationInfo>()
        var prevOfficeIdIntr = prevOfficeId
        while (initSr.isNotEmpty()) {
            val nextServiceRequestInfo = nextServiceRequest(initSr, prevOfficeIdIntr)
            result.add(nextServiceRequestInfo)
            initSr = initSr.filterNot { it.serviceRequestId == nextServiceRequestInfo.serviceRequestId }
            prevOfficeIdIntr = nextServiceRequestInfo.locationId
        }
        return result
    }

    private fun nextServiceRequest(initSr: List<ServiceRequestExecInfo>, prevOfficeId: String?): ServiceRequestExecLocationInfo {
        val maxPriority = initSr.map { it.priority }.max()!!
        val srWithMaxPriority = initSr.filter { it.priority == maxPriority }
        val locationInfos = srWithMaxPriority.flatMap { it.locationInfos }
        if (locationInfos.size == 1) {
            return locationInfos.first()
        }
        val minEstWaiting = locationInfos.map { it.estWaiting }.min()!!
        //находим все кабинеты с предположительным ожиданием в разбросе 15% от минимального
        //из них находим минимальный по коэф. дальности от предыд. местоположения
        val levelOfMinEstWaiting = minEstWaiting * 1.15
        val locationsWithMinLevelOfEstWaiting = locationInfos.filter { it.estWaiting <= levelOfMinEstWaiting }
        if (locationsWithMinLevelOfEstWaiting.size == 1) {
            return locationsWithMinLevelOfEstWaiting.first()
        }
        //пытаемся найти один с минимальным коэф. дальности
        val locationsWithDistanceCoef = locationsWithMinLevelOfEstWaiting.map {
            Pair(DistanceBetweenOfficesCalculator().calculate(prevOfficeId ?: RECEPTION, it.locationId), it)
        }
        val minDistanceCoef = locationsWithDistanceCoef.map { it.first }.min()
        val locationsWithMinDistanceCoef = locationsWithDistanceCoef.filter { it.first == minDistanceCoef }.map { it.second }
        if (locationsWithMinDistanceCoef.size == 1) {
            return locationsWithMinDistanceCoef.first()
        }
        //оцениваем полную "продолжительность" очереди. если и по такому критерию несколько, то берем первый в алфавитном порядке id кабинета
        val locationsWithEstWaitingTotal = locationsWithMinDistanceCoef.map { Pair(estWaitingInQueueWithType(it.locationId), it) }
        val minEstWaitingTotal = locationsWithEstWaitingTotal.map { it.first }.min()
        val locationsWithMinEstWaitingTotal = locationsWithEstWaitingTotal.filter { it.first == minEstWaitingTotal }.map { it.second }
        return locationsWithMinEstWaitingTotal.minBy { it.locationId }!!
    }

    /**
     * Предположительное время ожидания в очереди пациента с опр. степенью тяжести
     * Сумма приблизительных продолжительностей осмотра всех пациентов перед позицией в очереди, куда бы встал пациент с своей степенью тяжести
     * Если [severity] не указана, то полное время ожидания обслуживания всех в очереди
     */
    private fun estWaitingInQueueWithType(officeId: String, severity: Severity? = null): Int {
        val queue = queueService.queueItemsOfOffice(officeId)
        val inQueue = queue.filter { it.patientQueueStatus !in listOf(PatientQueueStatus.ON_OBSERVATION, PatientQueueStatus.GOING_TO_OBSERVATION) }
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
        observationType.parentCode
                ?: throw Exception("Observation type ${serviceRequest.code.code()} has no parentCode")
        val observationCategory = conceptService.parent(observationType)!!
        return observationCategory.priority ?: 0.5
    }

    /**
     * Информация о назначении
     * @param serviceRequestId id назначения
     * @param priority приоритет услуги
     * @param locationInfos информация о незакрытых кабинетах, где услуга м б проведена, [ServiceRequestExecLocationInfo]
     */
    private class ServiceRequestExecInfo(
            val serviceRequestId: String,
            val priority: Double,
            val locationInfos: List<ServiceRequestExecLocationInfo>
    )

    /**
     * Информация о кабинете, в котором может быть проведена услуга
     * @param serviceRequestId id назначения
     * @param locationId id кабинета
     * @param estWaiting предположительное ожидание в очереди
     */
    private class ServiceRequestExecLocationInfo(
            val serviceRequestId: String,
            val locationId: String,
            val estWaiting: Int
    )
}