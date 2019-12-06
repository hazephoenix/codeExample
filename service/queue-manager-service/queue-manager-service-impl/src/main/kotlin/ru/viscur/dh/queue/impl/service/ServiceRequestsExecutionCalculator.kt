package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.RECALC_NEXT_OFFICE_CONFIG_CODE
import ru.viscur.dh.datastorage.api.util.RECEPTION
import ru.viscur.dh.datastorage.api.util.allLocationIdsInGroup
import ru.viscur.dh.datastorage.api.util.isInspection
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.isInspectionOfResp
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY
import ru.viscur.dh.queue.impl.utils.DistanceCoefBetweenOfficesCalculator

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
        private val configService: ConfigService,
        private val serviceRequestService: ServiceRequestService
) {

    /**
     * Проставление/перепроставление порядка у невыполненных назначений в маршрутном листе пациента
     */
    fun calcServiceRequestExecOrders(patientId: String, prevOfficeId: String?): List<ServiceRequest> {
        val serviceRequestExecInfos = serviceRequestInfos(patientId)
        val sortedServiceRequestExecInfos = calcLocationAndOrder(serviceRequestExecInfos, prevOfficeId)
        return updateServiceRequests(sortedServiceRequestExecInfos)
    }

    /**
     * Обновляет кабинет и, если указан [updateOrder]=true, порядок
     */
    private fun updateServiceRequests(sortedServiceRequestExecInfos: List<ServiceRequestExecLocationInfo>, updateOrder: Boolean = true): List<ServiceRequest> =
            sortedServiceRequestExecInfos.mapIndexed { index, info ->
                resourceService.update(ResourceType.ServiceRequest, info.serviceRequestId) {
                    locationReference = listOf(referenceToLocation(info.locationId))
                    if (updateOrder) {
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
    private fun serviceRequestInfos(patientId: String, forQueueOnly: Boolean = false): List<ServiceRequestExecInfo> {
        val serviceRequests = if (forQueueOnly) serviceRequestService.activeForQueue(patientId) else serviceRequestService.active(patientId)
        val severity = patientService.severity(patientId)
        return serviceRequests.map { serviceRequest ->
            val serviceRequestId = serviceRequest.id
            val officeIds =
                    if (serviceRequest.isInspection()) {
                        locationIdsBySeverity(severity)
                    } else {
                        val byObservationType = locationService.byObservationType(serviceRequest.code.code())
                        //если не получилось определить кабинет для какой-то услуги, то отправляем в зону по степени тяжести
                        //это случай, например, осмотра анестезиолога - не привязан к кабинету, или если попала какая-то услуга, для которой нет соответсвия в кабинете
                        if (byObservationType.isEmpty()) locationIdsBySeverity(severity) else byObservationType
                    }
            val locationInfos = officeIds.map { officeId ->
                ServiceRequestExecLocationInfo(serviceRequestId, serviceRequest.code.code(), officeId, estWaitingInQueueWithType(officeId, severity))
            }
            if (locationInfos.isEmpty()) {
                throw Exception("ERROR. Can't find opened office for observation type '${serviceRequest.code.code()}'")
            }
            ServiceRequestExecInfo(serviceRequestId, priority(serviceRequest), locationInfos)
        }
    }

    /**
     * Места в зависимости от степени тяжести.
     * Для зеленых определяется зеленая зона, для красных - красная и т.д.
     */
    private fun locationIdsBySeverity(severity: Severity): List<String> {
        val locationType = severity.zoneForInspections
        return locationService.byLocationType(locationType).map { it.id }
    }

    fun calcNextOfficeId(patientId: String, prevOfficeId: String?, forQueueOnly: Boolean = false): String? {
        val serviceRequestExecInfos = serviceRequestInfos(patientId, forQueueOnly)
        if (serviceRequestExecInfos.isEmpty()) return null
        val nextServiceRequestInfo = nextServiceRequestWithRecalcOn(serviceRequestExecInfos, prevOfficeId ?: RECEPTION)
        return nextServiceRequestInfo.locationId
    }

    fun recalcOfficeForInspectionOfResp(patientId: String, severity: Severity) {
        val inspectionOfResp = serviceRequestService.active(patientId).find { it.isInspectionOfResp() }
                ?: throw Exception("not found inspection of responsible practitioner for patient with id '$patientId'")
        val locationType = severity.zoneForInspections
        val locationInfos = locationService.byLocationType(locationType).map {
            val officeId = it.id
            ServiceRequestExecLocationInfo(inspectionOfResp.id, inspectionOfResp.code.code(), officeId, estWaitingInQueueWithType(officeId, severity))
        }
        val sortedServiceRequestExecInfos = calcLocationAndOrder(listOf(
                ServiceRequestExecInfo(inspectionOfResp.id, priority(inspectionOfResp), locationInfos)
        ))
        updateServiceRequests(sortedServiceRequestExecInfos = sortedServiceRequestExecInfos, updateOrder = false)
    }

    /**
     * Однозначно определяет в каком кабинете нужно провести назначение, упорядочивает список назначений
     */
    private fun calcLocationAndOrder(initServiceRequests: List<ServiceRequestExecInfo>, prevOfficeId: String? = null): List<ServiceRequestExecLocationInfo> {
        return if (needRecalcNextOffice()) {
            calcLocationAndOrderWithRecalcOn(initServiceRequests, prevOfficeId ?: RECEPTION)
        } else {
            calcLocationAndOrderWithRecalcOff(initServiceRequests, prevOfficeId ?: RECEPTION)
        }
    }

    /**
     * Однозначно определяет в каком кабинете нужно провести назначение, упорядочивает список назначений
     * Случай, если включена настройка [RECALC_NEXT_OFFICE_CONFIG_CODE]
     */
    private fun calcLocationAndOrderWithRecalcOn(initServiceRequests: List<ServiceRequestExecInfo>, prevOfficeId: String): List<ServiceRequestExecLocationInfo> {
        var initSr = initServiceRequests
        val result = mutableListOf<ServiceRequestExecLocationInfo>()
        var prevOfficeIdIntr = prevOfficeId
        while (initSr.isNotEmpty()) {
            val nextServiceRequestInfo = nextServiceRequestWithRecalcOn(initSr, prevOfficeIdIntr)
            //сразу все назначения в кабинете, который определился как следующий
            val nextOfficeId = nextServiceRequestInfo.locationId
            val servReqsInNextOffice = initSr.flatMap { it.locationInfos }.filter { nextOfficeId == it.locationId }.sortedBy { it.code }
            result.addAll(servReqsInNextOffice)
            initSr = initSr.filterNot { it.serviceRequestId in servReqsInNextOffice.map { it.serviceRequestId } }
            prevOfficeIdIntr = nextOfficeId
        }
        return result
    }

    /**
     * Определение следующего назначения/кабинета при включенной настройке [RECALC_NEXT_OFFICE_CONFIG_CODE]
     */
    private fun nextServiceRequestWithRecalcOn(initSr: List<ServiceRequestExecInfo>, prevOfficeId: String): ServiceRequestExecLocationInfo {
        //если с максимальным приоритетом сделали фильтр по услугам и определился один кабинет
        val locationsWithMaxPriority = initSr.filterWithMaxPriority()
        if (locationsWithMaxPriority.distinctLocationSize() == 1) {
            return locationsWithMaxPriority.first()
        }
        //пытаемся найти один с минимальным временем * коэф. дальности
        val locationsWithEstDurationToDistanceCoef = locationsWithMaxPriority.map {
            val distanceCoef = DistanceCoefBetweenOfficesCalculator().calculate(prevOfficeId, it.locationId)
            Pair(it.estWaiting * distanceCoef, it)
        }
        val locationsWithMinValue = locationsWithEstDurationToDistanceCoef.filterWithMinFirstValue()
        if (locationsWithMinValue.distinctLocationSize() == 1) {
            return locationsWithMinValue.first()
        }
        //оцениваем полную "продолжительность" очереди. если и по такому критерию несколько, то берем первый в алфавитном порядке id кабинета
        return withMinEstWaitingTotal(locationsWithMinValue)
    }

    /**
     * Однозначно определяет в каком кабинете нужно провести назначение, упорядочивает список назначений
     * Случай, если выключена настройка [RECALC_NEXT_OFFICE_CONFIG_CODE]
     */
    private fun calcLocationAndOrderWithRecalcOff(initServiceRequests: List<ServiceRequestExecInfo>, prevOfficeId: String): List<ServiceRequestExecLocationInfo> {
        var initSr = initServiceRequests
        val result = mutableListOf<ServiceRequestExecLocationInfo>()
        var prevOfficeIdIntr = prevOfficeId
        while (initSr.isNotEmpty()) {
            val nextServiceRequestInfos = nextServiceRequestsWithRecalcOff(initSr, prevOfficeIdIntr)
            result.addAll(nextServiceRequestInfos)
            initSr = initSr.filterNot { it.serviceRequestId in nextServiceRequestInfos.map { it.serviceRequestId } }
            prevOfficeIdIntr = nextServiceRequestInfos.last().locationId
        }
        return result
    }

    /**
     * Определение следующих назначения/кабинета при выключенной настройке [RECALC_NEXT_OFFICE_CONFIG_CODE]
     * Если с макс приоритетом определился один кабинет, возвращаем все назначения в этом кабинете
     * Иначе назначения с одинаковым приоритетом упорядочиваются с помощью [calcLocationAndOrderWithRecalcOffForSamePriority]
     */
    private fun nextServiceRequestsWithRecalcOff(initSr: List<ServiceRequestExecInfo>, prevOfficeId: String): List<ServiceRequestExecLocationInfo> {
        val locationInfos = initSr.filterWithMaxPriority()
        if (locationInfos.distinctLocationSize() == 1) {
            return initSr.flatMap { it.locationInfos }.filter { locationInfos.first().locationId == it.locationId }.sortedBy { it.code }
        }
        return calcLocationAndOrderWithRecalcOffForSamePriority(initServiceRequests = locationInfos, prevOfficeId = prevOfficeId, iterationNumber = 0)
    }

    /**
     * Определение кабинетов и порядка для назначений с одним приоритетом
     * Функция итерационно обрабатывает входной список [initServiceRequests] в [result] до тех пор, пока все не переложит
     */
    private fun calcLocationAndOrderWithRecalcOffForSamePriority(initServiceRequests: List<ServiceRequestExecLocationInfo>, result: MutableList<ServiceRequestExecLocationInfo> = mutableListOf(), prevOfficeId: String, iterationNumber: Int): List<ServiceRequestExecLocationInfo> {
        val nextServiceRequestInfo = if (iterationNumber == 0) {
            //если рассматриваем первый кабинет в списке равнозначных по приоритету услуг
            //смотрим с мин. временем ожидания -> если несколько, то с мин коэф дальности -> если неск, то по общей очереди или по алфавиту
            nextWithRecalcOffForFirstIteration(initServiceRequests, prevOfficeId)
        } else {
            //если второй или последующий
            val locationIdsInGroup = allLocationIdsInGroup(prevOfficeId)
            val servReqsInLocationGroup = initServiceRequests.filter { it.locationId in locationIdsInGroup }
            if (servReqsInLocationGroup.isNotEmpty()) {
                //смотрим есть ли назначения в том же секторе/группе кабинетов - их проходим
                nextWithRecalcOffForNotFirstIterationForSameLocationGroup(servReqsInLocationGroup)
            } else {
                //если нет в той же группе то смотрим с мин дальностью -> если неск. то с мин временем -> если неск, то по общей очереди или по алфавиту
                nextWithRecalcOffForNotFirstIterationForOtherLocationGroup(initServiceRequests, prevOfficeId)
            }
        }
        //определили след. кабинет => перекладываем все назначения в этом кабинете из initServiceRequests в result
        var initSr = initServiceRequests
        val nextOfficeId = nextServiceRequestInfo.locationId
        val servReqsInNextOffice = initSr.filter { nextOfficeId == it.locationId }.sortedBy { it.code }
        result.addAll(servReqsInNextOffice)
        initSr = initSr.filterNot { it.serviceRequestId in servReqsInNextOffice.map { it.serviceRequestId } }

        //если исходный список пуст - все распределили. иначе делаем еще итерацию, но уже этот кабинет как предыдущий
        if (initSr.isEmpty()) return result
        return calcLocationAndOrderWithRecalcOffForSamePriority(initSr, result, nextOfficeId, iterationNumber + 1)
    }

    /**
     * Определение след. кабинета при выключенной настройке [RECALC_NEXT_OFFICE_CONFIG_CODE]
     * и первой итерации при определении порядка для равнозначных по приоритету услуг/кабинетов
     */
    private fun nextWithRecalcOffForFirstIteration(initServiceRequests: List<ServiceRequestExecLocationInfo>, prevOfficeId: String): ServiceRequestExecLocationInfo {
        val locationsWithMinEstWaiting = initServiceRequests.filterWithMinEstWaiting()
        if (locationsWithMinEstWaiting.distinctLocationSize() == 1) {
            return locationsWithMinEstWaiting.first()
        }
        //пытаемся найти один с минимальным коэф. дальности
        val locationsWithDistanceCoef = locationsWithMinEstWaiting.map {
            Pair(DistanceCoefBetweenOfficesCalculator().calculate(prevOfficeId ?: RECEPTION, it.locationId), it)
        }
        val locationsWithMinDistanceCoef = locationsWithDistanceCoef.filterWithMinFirstValue()
        if (locationsWithMinDistanceCoef.distinctLocationSize() == 1) {
            return locationsWithMinDistanceCoef.first()
        }
        return withMinEstWaitingTotal(locationsWithMinDistanceCoef)
    }

    /**
     * Определение след. кабинета при выключенной настройке [RECALC_NEXT_OFFICE_CONFIG_CODE]
     * и НЕ первой итерации при определении порядка для равнозначных по приоритету услуг/кабинетов
     * для назначений в одном секторе/группы кабинетов
     */
    private fun nextWithRecalcOffForNotFirstIterationForSameLocationGroup(initServiceRequests: List<ServiceRequestExecLocationInfo>): ServiceRequestExecLocationInfo {
        //пытаемся найти с мин. временем ожидания в очереди
        val locationsWithMinEstWaiting = initServiceRequests.filterWithMinEstWaiting()
        if (locationsWithMinEstWaiting.distinctLocationSize() == 1) {
            return locationsWithMinEstWaiting.first()
        }
        //смотрим на общую продолжительность очереди или берем первый по алфавиту
        return withMinEstWaitingTotal(locationsWithMinEstWaiting)
    }

    /**
     * Определение след. кабинета при выключенной настройке [RECALC_NEXT_OFFICE_CONFIG_CODE]
     * и НЕ первой итерации при определении порядка для равнозначных по приоритету услуг/кабинетов
     * для назначений в другом секторе/группы кабинетов
     */
    private fun nextWithRecalcOffForNotFirstIterationForOtherLocationGroup(initServiceRequests: List<ServiceRequestExecLocationInfo>, prevOfficeId: String): ServiceRequestExecLocationInfo {
        //пытаемся найти один с минимальным коэф. дальности
        val locationsWithDistanceCoef = initServiceRequests.map {
            Pair(DistanceCoefBetweenOfficesCalculator().calculate(prevOfficeId ?: RECEPTION, it.locationId), it)
        }
        val minDistanceCoef = locationsWithDistanceCoef.map { it.first }.min()
        val locationsWithMinDistanceCoef = locationsWithDistanceCoef.filter { it.first == minDistanceCoef }.map { it.second }
        if (locationsWithMinDistanceCoef.distinctLocationSize() == 1) {
            return locationsWithMinDistanceCoef.first()
        }
        //если неск. то с мин. временем ожидания
        val locationsWithMinEstWaiting = locationsWithMinDistanceCoef.filterWithMinEstWaiting()
        if (locationsWithMinEstWaiting.distinctLocationSize() == 1) {
            return locationsWithMinEstWaiting.first()
        }
        //смотрим на общую продолжительность очереди или берем первый по алфавиту
        return withMinEstWaitingTotal(locationsWithMinDistanceCoef)
    }

    private fun withMinEstWaitingTotal(initSr: List<ServiceRequestExecLocationInfo>): ServiceRequestExecLocationInfo {
        //оцениваем полную "продолжительность" очереди. если и по такому критерию несколько, то берем первый в алфавитном порядке id кабинета
        val locationsWithEstWaitingTotal = initSr.map { Pair(estWaitingInQueueWithType(it.locationId), it) }
        return locationsWithEstWaitingTotal.sortedWith(compareBy({ it.first }, { it.second.locationId }, { it.second.code })).first().second
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
        if (serviceRequest.isInspectionOfResp()) {
            return 0.0
        }
        val observationType = conceptService.byCodeableConcept(serviceRequest.code)
        observationType.parentCode
                ?: throw Exception("Observation type ${serviceRequest.code.code()} has no parentCode")
        val observationCategory = conceptService.parent(observationType)!!
        return observationCategory.priority ?: 0.5
    }

    private fun needRecalcNextOffice() = configService.readBool(RECALC_NEXT_OFFICE_CONFIG_CODE)

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
     * @param code тип обследования
     * @param locationId id кабинета
     * @param estWaiting предположительное ожидание в очереди
     */
    private class ServiceRequestExecLocationInfo(
            val serviceRequestId: String,
            val code: String,
            val locationId: String,
            val estWaiting: Int
    )

    private fun List<ServiceRequestExecLocationInfo>.distinctLocationSize() = this.map { it.locationId }.distinct().size

    private fun List<Pair<Double, ServiceRequestExecLocationInfo>>.filterWithMinFirstValue(): List<ServiceRequestExecLocationInfo> {
        val minValue = this.map { it.first }.min()
        return this.filter { it.first == minValue }.map { it.second }
    }

    private fun List<ServiceRequestExecLocationInfo>.filterWithMinEstWaiting(): List<ServiceRequestExecLocationInfo> {
        val minEstWaiting = this.map { it.estWaiting }.min()!!
        return this.filter { it.estWaiting == minEstWaiting }
    }

    private fun List<ServiceRequestExecInfo>.filterWithMaxPriority(): List<ServiceRequestExecLocationInfo> {
        val maxPriority = this.map { it.priority }.max()!!
        return this.filter { it.priority == maxPriority }.flatMap { it.locationInfos }
    }
}