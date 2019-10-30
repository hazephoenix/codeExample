package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import javax.persistence.*

@Service
class CarePlanServiceImpl : CarePlanService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun current(patientId: String): CarePlan? {
        val query = em.createNativeQuery("""
            select cp.resource
            from CarePlan cp
            where cp.resource -> 'subject' ->> 'reference' = :patientRef
                and (cp.resource ->> 'status' = :active
                        OR cp.resource ->> 'status' = :waiting_results
                        OR cp.resource ->> 'status' = :results_are_ready)
        """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("active", CarePlanStatus.active.toString())
        query.setParameter("waiting_results", CarePlanStatus.waiting_results.toString())
        query.setParameter("results_are_ready", CarePlanStatus.results_are_ready.toString())
        return query.fetchResource()
    }
}