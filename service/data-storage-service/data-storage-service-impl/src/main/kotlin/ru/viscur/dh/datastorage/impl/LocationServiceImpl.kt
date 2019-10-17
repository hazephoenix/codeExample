package ru.viscur.dh.datastorage.impl

import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.enums.ResourceType
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 15.10.2019 12:10 by SherbakovaMA
 */
class LocationServiceImpl(
        private val resourceService: ResourceService
) : LocationService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun byId(id: String): Location = resourceService.byId(ResourceType.Location, id)

    override fun withPatientInLastPatientInfo(patientId: String): List<Location> {
        val query = em.createNativeQuery("""
            select r.resource
            from location r
            where r.resource->'extension'->'lastPatientInfo'->'subject'->>'reference'= :patientRef
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResourceList()
    }
}