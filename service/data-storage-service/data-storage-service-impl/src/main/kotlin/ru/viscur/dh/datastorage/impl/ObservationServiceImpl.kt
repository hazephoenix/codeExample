package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.*
import ru.viscur.dh.fhir.model.entity.*
import javax.persistence.*

@Service
class ObservationServiceImpl(
        private val resourceService: ResourceService
) : ObservationService {

    @PersistenceContext(name = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun create(observation: Observation): Observation? {
        return resourceService.create(observation)
    }

    override fun update(observation: Observation): Observation? {
        TODO("implement")
    }

    override fun findServiceRequest(observation: Observation): ServiceRequest? {
        val query = em.createNativeQuery("""
            select r.resource
            from serviceRequest r
            where r.resource ->> 'id' = :basedOnId
            """)
        query.setParameter("basedOnId", observation.basedOn?.id)
        return query.fetchResource()
    }
}