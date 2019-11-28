package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.criticalTimeForDeletingNextOfficeForPatientsInfo
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 15.10.2019 12:10 by SherbakovaMA
 */
@Service
class LocationServiceImpl(
        private val resourceService: ResourceService,
        private val conceptService: ConceptService
) : LocationService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun byId(id: String): Location = resourceService.byId(ResourceType.Location, id)

    override fun withPatientInNextOfficeForPatientsInfo(patientId: String): List<Location> {
        val query = em.createNativeQuery("""
            select resource
            from
                (select jsonb_array_elements(r.resource->'extension' ->'nextOfficeForPatientsInfo') as nextOffice, r.resource as resource
                 from location r) nextOfficeT
            where nextOffice->'subject'->>'reference' = :patientRef
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResourceList()
    }

    override fun withOldNextOfficeForPatientsInfo(): List<Location> {
        val query = em.createNativeQuery("""
            select resource
                from
            (select jsonb_array_elements(r.resource->'extension' ->'nextOfficeForPatientsInfo') as nextOffice, r.resource as resource
            from location r) nextOfficeT
            where (nextOfficeT.nextOffice->>'fireDate')\:\:bigint <= :criticalTime
            """)
        query.setParameter("criticalTime", criticalTimeForDeletingNextOfficeForPatientsInfo().time)
        return query.fetchResourceList()
    }

    override fun byObservationType(type: String): List<String> {
        val typeConcept = conceptService.byCode(ValueSetName.OBSERVATION_TYPES, type)
        val observationCategory = typeConcept.parentCode
        val query = em.createNativeQuery("""
                select id
                from (select jsonb_array_elements(r.resource -> 'extension' -> 'observationType') obsType, r.id
                      from location r 
                      where r.resource -> 'extension' -> 'observationType' <> 'null'
                        and r.resource ->> 'status' <> '${LocationStatus.CLOSED}'
                      ) obsInfo
                where obsInfo.obsType ->> 'code' in (:observationType, :observationCategory)
            """)
        query.setParameter("observationType", type)
        query.setParameter("observationCategory", observationCategory)
        return query.resultList as List<String>
    }

    override fun byLocationType(type: String): List<Location> {
        val query = em.createNativeQuery("""
                select r.resource from
                (select jsonb_array_elements((jsonb_array_elements(resource->'type')->'coding'))->>'code' code, resource
                from location) r
                where r.code = :type
            """)
        query.setParameter("type", type)
        return query.fetchResourceList()
    }
}