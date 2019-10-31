package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.annotation.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import javax.persistence.*

@Service
class ServiceRequestServiceImpl(
        private val resourceService: ResourceService,
        private val carePlanService: CarePlanService
) : ServiceRequestService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun all(patientId: String): List<ServiceRequest> {
        //active clinicalImpression -> carePlan -> all serviceRequests
        val query = em.createNativeQuery("""
            select sr.resource
            from serviceRequest sr
            where 'ServiceRequest/' || sr.id in (
                select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from carePlan cp
                where 'CarePlan/' || cp.id in (
                    select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                    from clinicalImpression ci
                    where ci.resource -> 'subject' ->> 'reference' = :patientRef
                      and ci.resource ->> 'status' = 'active'
                )
            )
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResourceList()
    }

    override fun active(patientId: String, officeId: String): List<ServiceRequest> {
        //active clinicalImpression -> carePlan -> active serviceRequests in office
        val query = em.createNativeQuery("""
            select sr.resource
            from serviceRequest sr
            where 'ServiceRequest/' || sr.id in (
                select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from carePlan cp
                where 'CarePlan/' || cp.id in (
                    select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                    from clinicalImpression ci
                    where ci.resource -> 'subject' ->> 'reference' = :patientRef
                      and ci.resource ->> 'status' = 'active'
                )
              )
              and sr.resource ->> 'status' = 'active'
              and :officeRef in (
                select jsonb_array_elements(sIntr.resource -> 'locationReference') ->> 'reference'
                from serviceRequest sIntr
                where sIntr.id = sr.id)
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("officeRef", "Location/$officeId")
        return query.fetchResourceList()
    }


    override fun active(patientId: String): List<ServiceRequest> {
        //active clinicalImpression -> carePlan -> active serviceRequests
        val query = em.createNativeQuery("""
            select sr.resource
            from serviceRequest sr
            where 'ServiceRequest/' || sr.id in (
                select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from carePlan cp
                where 'CarePlan/' || cp.id in (
                    select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                    from clinicalImpression ci
                    where ci.resource -> 'subject' ->> 'reference' = :patientRef
                      and ci.resource ->> 'status' = 'active'
                )
            )
            and sr.resource->>'status' = 'active'
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResourceList()
    }

    @Tx
    override fun add(patientId: String, serviceRequestList: List<ServiceRequest>): CarePlan {
        return carePlanService.current(patientId)?.let { carePlan ->
            val serviceRequests = serviceRequestList.map { resourceService.create(it) }
            val activities = serviceRequests.map { CarePlanActivity(Reference(it)) }

            resourceService.update(ResourceType.CarePlan, carePlan.id) {
                status = CarePlanStatus.active // results_are_ready -> active
                activity = activities + activity
            }
        } ?: throw Error("No active CarePlan found")
    }

    @Tx
    override fun updateStatusByObservation(observation: Observation): ServiceRequest =
            observation.basedOn?.id?.let { serviceRequestId ->
                resourceService.update(ResourceType.ServiceRequest, serviceRequestId) {
                    status = when (observation.status) {
                        ObservationStatus.final -> ServiceRequestStatus.completed
                        else -> ServiceRequestStatus.waiting_result
                    }
                }
            } ?: throw Error("Not found ServiceRequest by Observation.basedOn.id: '${observation.basedOn?.id}'")
}