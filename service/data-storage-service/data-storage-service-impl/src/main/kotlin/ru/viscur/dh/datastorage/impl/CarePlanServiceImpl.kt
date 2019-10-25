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

    override fun getActiveByPractitioner(practitionerId: String): List<CarePlan> {
        val query = em.createNativeQuery("""
            select cp.resource
            from CarePlan cp
            where cp.resource -> 'author' ->> 'id' = :practitionerId
                and (cp.resource ->> 'status' = :active
                        OR cp.resource ->> 'status' = :waiting_results
                        OR cp.resource ->> 'status' = :results_are_ready)
        """)
        query.setParameter("practitionerId", practitionerId)
        query.setParameter("active", CarePlanStatus.active.toString())
        query.setParameter("waiting_results", CarePlanStatus.waiting_results.toString())
        query.setParameter("results_are_ready", CarePlanStatus.results_are_ready.toString())
        return query.fetchResourceList()
    }

    override fun getActive(patientId: String): CarePlan? {
        val query = em.createNativeQuery("""
            select cp.resource
            from CarePlan cp
            where cp.resource -> 'subject' ->> 'id' = :patientId
                and (cp.resource ->> 'status' = :active
                        OR cp.resource ->> 'status' = :waiting_results
                        OR cp.resource ->> 'status' = :results_are_ready)
        """)
        query.setParameter("patientId", patientId)
        query.setParameter("active", CarePlanStatus.active.toString())
        query.setParameter("waiting_results", CarePlanStatus.waiting_results.toString())
        query.setParameter("results_are_ready", CarePlanStatus.results_are_ready.toString())
        return query.fetchResource()
    }
}