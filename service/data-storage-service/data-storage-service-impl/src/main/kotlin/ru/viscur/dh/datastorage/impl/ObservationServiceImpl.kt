package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import javax.persistence.*

@Service
class ObservationServiceImpl(private val resourceService: ResourceService) : ObservationService {

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

    /**
     * Зарегистрировать обследование
     *
     * Обследование обязательно должно содержать поле basedOn
     * со ссылкой на ServiceRequest
     */
    override fun create(observation: Observation): Observation? {
        updateServiceRequestStatus(observation)
        return resourceService.create(observation)
    }

    /**
     * Обновить обследование (добавить результаты)
     */
    override fun update(observation: Observation): Observation? {
        return resourceService.byId(ResourceType.Observation, observation.id)
                .let {
                    it.performer = it.performer.union(observation.performer).toList().distinctBy { item -> item.id }
                    it.status = observation.status
                    it.valueBoolean = observation.valueBoolean
                    it.valueInteger = observation.valueInteger
                    it.valueQuantity = observation.valueQuantity
                    it.valueSampledData = observation.valueSampledData
                    it.valueString = observation.valueString

                    updateServiceRequestStatus(it)
                    resourceService.update(it)
                }
    }

    /**
     * Обновить статус направления на обследование и маршрутного листа
     */
    private fun updateServiceRequestStatus(observation: Observation) =
            observation.basedOn?.id?.let { serviceRequestId ->
                // Обновить статус направления на обследование
                try {
                    resourceService.byId(ResourceType.ServiceRequest, serviceRequestId)
                            .let {
                                when (observation.status) {
                                    ObservationStatus.final -> it.status = ServiceRequestStatus.completed
                                    else -> it.status = ServiceRequestStatus.waiting_result
                                }
                                resourceService.update(it)
                                // Обновить статус маршрутного листа
                                updateCarePlan(it.id)
                            }
                } catch (e: Exception) {
                    throw Error("ServiceRequest.id (basedOn) does not exist")
                }
            } ?: throw Error("No correct ServiceRequest.id provided (basedOn)")

    /**
     * Обновить соответствующий направлению [ServiceRequest] маршрутный лист [CarePlan]
     */
    private fun updateCarePlan(serviceRequestId: String) {
        getCarePlanByServiceRequestId(serviceRequestId)?.let { carePlan ->
            carePlan.status = when (getUncompletedServiceRequests(carePlan.id).isEmpty()) {
                false -> CarePlanStatus.waiting_results
                true -> CarePlanStatus.results_are_ready
            }
            resourceService.update(carePlan)
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