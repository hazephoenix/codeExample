package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.SpecimenStatus
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*

/**
 * Created at 04.10.2019 9:35 by SherbakovaMA
 *
 * Проба для анализа
 *
 * @param status статус, [SpecimenStatus]
 * @param type код вида материала, из которого взят образец
 * @param subject пациент, ссылка на [Patient]
 * @param receivedTime дата-время взятия материала
 */
class Specimen @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>?,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Specimen.id,
        @JsonProperty("status") val status: SpecimenStatus,
        @JsonProperty("type") val type: CodeableConcept,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("receivedTime") val receivedTime: Date
//todo много полей, добавим по необходимости
) : BaseResource(id, identifier, resourceType)