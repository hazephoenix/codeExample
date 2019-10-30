package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Concept
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 16.10.2019 17:53 by SherbakovaMA
 */
@Service
class ConceptServiceImpl(private val resourceService: ResourceService) : ConceptService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun byCodeableConcept(codeableConcept: CodeableConcept): Concept {
        val coding = codeableConcept.coding.first()
        return resourceService.single(
                ResourceType.Concept, RequestBodyForResources(filter = mapOf(
                "code" to coding.code,
                "system" to coding.system
        )))
    }

    override fun parent(concept: Concept): Concept? = concept.parentCode?.let {
        resourceService.single(
                ResourceType.Concept, RequestBodyForResources(filter = mapOf(
                "code" to concept.parentCode!!,
                "system" to concept.system
        )))
    }

    override fun byCode(valueSetId: String, code: String): Concept =
            resourceService.single(
                    ResourceType.Concept, RequestBodyForResources(filter = mapOf(
                    "code" to code,
                    "system" to "ValueSet/$valueSetId"
            )))

    override fun byParent(valueSet: ValueSetName, parentCode: String?): List<Concept> = resourceService.all(
            ResourceType.Concept, RequestBodyForResources(filter = mapOf(
            "parentCode" to parentCode,
            "system" to "ValueSet/${valueSet.id}"
    )))

    override fun byAlternative(valueSetId: String, realAlternatives: List<String>): List<String> {
        if (realAlternatives.isEmpty()) return listOf()
        val realAlternativesStr = realAlternatives.mapIndexed { index, code -> "(?${index + 1})" }.joinToString(", ")
        val systemParamNumber = realAlternatives.size + 1
        val q = em.createNativeQuery("""
        select resource ->> 'code' code
        from (select * from (values $realAlternativesStr) c (real_alt)) c
             join
         (select jsonb_array_elements(r.resource -> 'alternatives') ->> 0 alt, r.resource
          from concept r
          where r.resource ->> 'system' = ?$systemParamNumber) alts
         on c.real_alt ilike '%' || alts.alt || '%'
        """.trimIndent())
        realAlternatives.forEachIndexed { index, code ->
            q.setParameter(index + 1, code)
        }
        q.setParameter(systemParamNumber, "ValueSet/$valueSetId")
        return q.resultList as List<String>
    }
}