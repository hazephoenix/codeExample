package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 03.10.2019 16:55 by SherbakovaMA
 *
 * Информация для начисления стоимости услуг пациенту
 * [info](http://fhir-ru.github.io/chargeitem.html)
 *
 * @param subject пациент, которому услуги предоставлялись, ссылка на [Patient]
 * @param service какие услуги были проведены, ссылки на [Procedure]/[Observation]/[DiagnosticReport]
 */
class ChargeItem @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.ChargeItem.id,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("service") val service: List<Reference>
) : BaseResource(id, identifier, resourceType)