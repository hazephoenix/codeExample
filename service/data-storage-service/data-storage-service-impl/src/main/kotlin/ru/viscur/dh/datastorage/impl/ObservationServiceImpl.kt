package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import javax.persistence.*

@Service
class ObservationServiceImpl(
        private val resourceService: ResourceService,
        private val serviceRequestService: ServiceRequestService
) : ObservationService {

    @PersistenceContext
    private lateinit var em: EntityManager

    /**
     * Найти все обследования по id пациента и статусу обследования
     */
    override fun findByPatientAndStatus(patientId: String, status: ObservationStatus): List<Observation?> {
        val query = em.createNativeQuery("""
            select r.resource
                from Observation r
                where r.resource -> 'basedOn' ->> 'reference' in (
                    select 'ServiceRequest/' || sr.id
                    from ServiceRequest sr
                    where 'ServiceRequest/' || sr.id in (
                        select
                            jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                        from CarePlan cp
                        where cp.resource -> 'subject' ->> 'id' = :patientId
                        )
                )
                and r.resource ->> 'status' = :status
        """)
        query.setParameter("patientId", patientId)
        query.setParameter("status", status.toString())
        return query.fetchResourceList()
    }

    override fun byBaseOnServiceRequestId(id: String): Observation? {
        val query = em.createNativeQuery("""
            select r.resource
                from Observation r
                where r.resource -> 'basedOn' ->> 'reference' = :serviceRequestRef
        """)
        query.setParameter("serviceRequestRef", "ServiceRequest/$id")
        return query.fetchResourceList<Observation>().firstOrNull()
    }

    /**
     * Зарегистрировать обследование
     *
     * Обследование обязательно должно содержать поле basedOn
     * со ссылкой на ServiceRequest
     */
    override fun create(patientId: String, observation: Observation): Observation {
        observation.apply {
            subject = referenceToPatient(patientId)
        }
        updateRelated(patientId, observation)
        return resourceService.create(observation)
    }

    /**
     * Обновить обследование (добавить результаты)
     */
    override fun update(patientId: String, observation: Observation): Observation {
        val updatedObservation = resourceService.update(ResourceType.Observation, observation.id) {
            performer = (performer + observation.performer).distinctBy { item -> item.id }
            status = observation.status
            valueBoolean = observation.valueBoolean
            valueInteger = observation.valueInteger
            valueQuantity = observation.valueQuantity
            valueSampledData = observation.valueSampledData
            valueString = observation.valueString
        }
        updateRelated(patientId, updatedObservation)
        return updatedObservation
    }

    /**
     * Обновить связанные ресурсы -
     *  статус направления на обследование и маршрутного листа
     */
    private fun updateRelated(patientId: String, observation: Observation){
        // Обновить статус направления на обследование
        val updatedServiceRequest = serviceRequestService.updateStatusByObservation(observation)
        // Обновить статус маршрутного листа
        updateCarePlan(patientId, updatedServiceRequest.id)
    }


    /**
     * Обновить соответствующий направлению [ServiceRequest] маршрутный лист [CarePlan]
     */
    private fun updateCarePlan(patientId: String, serviceRequestId: String) {
        getCarePlanByServiceRequestId(serviceRequestId)?.let { carePlan ->
            resourceService.update(ResourceType.CarePlan, carePlan.id) {
                val serviceRequests = serviceRequestService.all(patientId)
                val serviceRequestsWithoutResp = serviceRequests.filter { it.performer.isNullOrEmpty() }
                status = when {
                    serviceRequestsWithoutResp.any { it.status == ServiceRequestStatus.active } -> CarePlanStatus.active
                    serviceRequestsWithoutResp.all { it.status == ServiceRequestStatus.completed } -> CarePlanStatus.results_are_ready
                    else -> CarePlanStatus.waiting_results
                }
            }
        }
    }

    /**
     * Получить [CarePlan] по id [ServiceRequest]
     */
    private fun getCarePlanByServiceRequestId(serviceRequestId: String): CarePlan? {
        val query = em.createNativeQuery("""
                select r.resource
                from CarePlan r
                where 'ServiceRequest/' || :servReqId in (
                    select
                        jsonb_array_elements(rIntr.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                    from CarePlan rIntr
                    where rIntr.id = r.id
                )
        """)
        query.setParameter("servReqId", serviceRequestId)
        return query.fetchResource()
    }

    /**
     * Находит в CarePlan.activity незавершенные ServiceRequest
     */
    fun getUncompletedServiceRequests(carePlanId: String): List<ServiceRequest> {
        val query = em.createNativeQuery("""
            select sr.resource from ServiceRequest sr
            where sr.resource ->>'status' != 'completed' and 'ServiceRequest/' || sr.id in (
                select
                    jsonb_array_elements(rIntr.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from CarePlan rIntr
                where rIntr.id = :carePlanId
            )
        """)
        query.setParameter("carePlanId", carePlanId)
        return query.fetchResourceList()
    }
}