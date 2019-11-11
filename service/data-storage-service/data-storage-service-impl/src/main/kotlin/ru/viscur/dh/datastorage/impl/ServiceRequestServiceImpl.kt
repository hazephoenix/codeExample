package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.transaction.desc.config.annotation.Tx
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
        //коды непройденных назначений:
        //  active clinicalImpression -> carePlan -> active serviceRequests in office -> code
        //ищутся по code или parentCode, указанных в проводимых обследованиях кабинета
        val query = em.createNativeQuery("""
            select sr.resource
            from (
                     select jsonb_array_elements(r.resource -> 'code' -> 'coding') ->> 'code' code, r.resource
                     from serviceRequest r
                     where 'ServiceRequest/' || r.id in (
                         select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                         from carePlan cp
                         where 'CarePlan/' || cp.id in (
                             select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                             from clinicalImpression ci
                             where ci.resource -> 'subject' ->> 'reference' = :patientRef
                               and ci.resource ->> 'status' = 'active'
                         )
                     )
                       and r.resource ->> 'status' = 'active'
                 ) sr
            join (
                select r.resource ->> 'code' code, r.resource ->> 'parentCode' parentCode
                from concept r
                where r.resource ->> 'system' = 'ValueSet/Observation_types'
            ) c
            on sr.code = c.code
            join (
                select jsonb_array_elements(r.resource -> 'extension' -> 'observationType') ->> 'code' obsType
                from location r
                where r.resource -> 'extension' -> 'observationType' <> 'null'
                  and r.id = :officeId
            ) of
            on c.code = of.obsType or c.parentCode = of.obsType
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("officeId", officeId)
        return query.fetchResourceList()
    }

    override fun activeByObservationCategory(patientId: String, parentCode: String): List<ServiceRequest> {
        //  active clinicalImpression -> carePlan -> active serviceRequests у которых коды имеют указанный parentCode
        val query = em.createNativeQuery("""
            select sr.resource
            from (
                     select jsonb_array_elements(r.resource -> 'code' -> 'coding') ->> 'code' code, r.resource
                     from serviceRequest r
                     where 'ServiceRequest/' || r.id in (
                         select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                         from carePlan cp
                         where 'CarePlan/' || cp.id in (
                             select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                             from clinicalImpression ci
                             where ci.resource -> 'subject' ->> 'reference' = :patientRef
                               and ci.resource ->> 'status' = 'active'
                         )
                     )
                       and r.resource ->> 'status' = 'active'
                 ) sr
            join (
                select r.resource ->> 'code' code
                from concept r
                where r.resource ->> 'system' = 'ValueSet/Observation_types'
                    and r.resource ->> 'parentCode' = :parentCode
            ) c
            on sr.code = c.code
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("parentCode", parentCode)
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
        } ?: throw Error("No active CarePlan found for patient with id '$patientId'")
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

    @Tx
    override fun cancelServiceRequests(patientId: String, officeId: String): List<ServiceRequest> {
        val activeInOffice = active(patientId, officeId)
        return activeInOffice.map {
            resourceService.update(ResourceType.ServiceRequest, it.id) {
                status = ServiceRequestStatus.cancelled
            }
        }
    }

    @Tx
    override fun cancelServiceRequest(id: String): ServiceRequest =
            resourceService.update(ResourceType.ServiceRequest, id) {
                if (status in listOf(ServiceRequestStatus.active, ServiceRequestStatus.waiting_result)) {
                    status = ServiceRequestStatus.cancelled
                }
            }
}