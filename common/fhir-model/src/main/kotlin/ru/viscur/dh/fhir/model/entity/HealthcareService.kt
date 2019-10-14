package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 18:28 by SherbakovaMA
 *
 * Услуга
 * [info](http://fhir-ru.github.io/healthcareservice.html)
 *
 * @param type тип услуги (или несколько)
 * @param name наименование услуги
 * @param location местоположения, где может быть предоставлена данная услуга, ссылка на место [Location]
 */
class HealthcareService @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.HealthcareService.id,
        @JsonProperty("name") val name: String,
        @JsonProperty("type") val type: List<CodeableConcept>,
        @JsonProperty("location") val location: List<Reference>
) : BaseResource(id, identifier, resourceType)