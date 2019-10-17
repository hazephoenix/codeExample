package ru.viscur.dh.datastorage.impl

import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Concept
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept

/**
 * Created at 16.10.2019 17:53 by SherbakovaMA
 */
class ConceptServiceImpl(private val resourceService: ResourceService) : ConceptService {

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
}