package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import javax.persistence.*

@Service
class CarePlanServiceImpl(
        private val resourceService: ResourceService
) : CarePlanService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun current(patientId: String): CarePlan? {
        val query = em.createNativeQuery("""
            select cp.resource
            from CarePlan cp
            where cp.resource -> 'subject' ->> 'reference' = :patientRef
                and cp.resource ->> 'status' in (:active, :waiting_results, :results_are_ready)
        """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("active", CarePlanStatus.active.toString())
        query.setParameter("waiting_results", CarePlanStatus.waiting_results.toString())
        query.setParameter("results_are_ready", CarePlanStatus.results_are_ready.toString())
        return query.fetchResource()
    }

    override fun byServiceRequestId(serviceRequestId: String): CarePlan? {
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

    override fun complete(carePlanId: String) {
        resourceService.update(ResourceType.CarePlan, carePlanId) {
            status = CarePlanStatus.completed
        }
    }
}