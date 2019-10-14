package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Coding
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 04.10.2019 15:23 by SherbakovaMA
 *
 * Сопоставление типа услуги/процедуры и цены
 *
 * @param code тип услуги
 * @param price цена
 */
class ProcedurePrice @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>?,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Patient.id,
        @JsonProperty("code") val code: Coding,
        @JsonProperty("price") val price: Double
) : BaseResource(id, identifier, resourceType)