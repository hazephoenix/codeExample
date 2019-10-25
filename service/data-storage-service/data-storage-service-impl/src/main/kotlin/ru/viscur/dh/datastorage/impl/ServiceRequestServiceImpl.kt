package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.valueSets.*
import javax.persistence.*

@Service
class ServiceRequestServiceImpl(
        private val resourceService: ResourceService,
        private val locationService: LocationService
) : ServiceRequestService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(patientId: String): List<ServiceRequest> {
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

    override fun getActive(patientId: String, officeId: String): List<ServiceRequest> {
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


    override fun getActive(patientId: String): List<ServiceRequest> {
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

    // TODO: создать услугу по коду специальности и привязать врача к кабинету
    override fun createForPractitioner(practitionerRef: Reference): ServiceRequest =
            resourceService.create(
                    ServiceRequest(
                            code = CodeableConcept(
                                    code = "Surgeon",
                                    systemId = ValueSetName.OBSERVATION_TYPES.id,
                                    display = "Осмотр хирурга"
                            ),
                            locationReference = listOf(Reference(locationService.byObservationType("Surgeon"))),
                            extension = ServiceRequestExtension(executionOrder = 1)
                    )
            )
}