package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 03.10.2019 16:47 by SherbakovaMA
 *
 * Процедура (услуга)
 * [info](http://fhir-ru.github.io/procedure.html)
 *
 * @param identifier идентификатор услуги (может включать номер)
 * @param code код
 * @param location место проведение услуги, ссылка на [Location]
 */
class Procedure @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>,
        @JsonProperty("resourceType") resourceType: ResourceType = ResourceType.Procedure,
        @JsonProperty("code") val code: CodeableConcept,
        @JsonProperty("location") val location: Reference
) : BaseResource(id, identifier, resourceType)