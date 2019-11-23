package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 23.10.2019 17:18 by SherbakovaMA
 */
@Service
class PractitionerServiceImpl(
        private val resourceService: ResourceService
) : PractitionerService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun all(withBlocked: Boolean): List<Practitioner> {
        val whereClause = if(withBlocked) "" else "where (r.resource->'extension'->>'blocked')\\:\\:boolean = false"
        val query = em.createNativeQuery("""
            select r.resource
            from practitioner r
            $whereClause""")
        return query.fetchResourceList()
    }

    override fun create(practitioner: Practitioner): Practitioner = resourceService.create(practitioner.apply {
        id = genId()
    })

    override fun byId(id: String): Practitioner = resourceService.byId(ResourceType.Practitioner, id)

    override fun byQualifications(codes: List<String>): List<Practitioner> {
        if (codes.isEmpty()) return listOf()
        val codesStr = codes.mapIndexed { index, code -> "(?${index + 1})" }.joinToString(", ")
        val q = em.createNativeQuery("""
            select resource from
            (select * from (values $codesStr) q (qual)) q
            join
            (
                select jsonb_array_elements(r.resource->'qualification'->'code'->'coding')->>'code' pr_qual, r.resource resource 
                from practitioner r
                where (r.resource->'extension'->>'blocked')\:\:boolean = false
            ) pr
            on q.qual = pr.pr_qual
        """.trimIndent())
        codes.forEachIndexed { index, code ->
            q.setParameter(index + 1, code)
        }
        return q.fetchResourceList()
    }

    override fun updateBlocked(practitionerId: String, value: Boolean): Practitioner =
            resourceService.update(ResourceType.Practitioner, practitionerId) {
                extension.blocked = value
            }
}