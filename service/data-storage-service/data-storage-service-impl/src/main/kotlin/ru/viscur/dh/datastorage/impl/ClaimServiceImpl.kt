package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import javax.persistence.*

@Service
class ClaimServiceImpl : ClaimService {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getActive(patientId: String): Claim? {
        val query = em.createNativeQuery("""
            select c.resource
            from Claim c
            where c.resource ->> 'status' = :status
                and c.resource -> 'patient' ->> 'id' = :patientId
            """)
        query.setParameter("status", ClaimStatus.active.toString())
        query.setParameter("patientId", patientId)
        return query.fetchResource()
    }
}