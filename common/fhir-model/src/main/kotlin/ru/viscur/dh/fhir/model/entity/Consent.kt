package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ConsentStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.sql.Timestamp

/**
 * Created at 01.10.2019 13:24 by SherbakovaMA
 *
 * Соглашение
 * [info](http://fhir-ru.github.io/consent.html)
 *
 * @param category категория
 * @param status статус
 * @param dateTime дата
 * @param patient пациент, ссылка на [Patient]
 * @param performer мед. работник, ссылка на [Practitioner]
 * @param organization организация (или несколько), ссылка на [Organization]
 */
class Consent @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Consent.id,
        @JsonProperty("category") val category: List<CodeableConcept>,
        @JsonProperty("status") val status: ConsentStatus = ConsentStatus.active,
        @JsonProperty("dateTime") val dateTime: Timestamp,
        @JsonProperty("patient") val patient: Reference,
        @JsonProperty("performer") val performer: Reference? = null,
        @JsonProperty("organization") val organization: List<Reference>
) : BaseResource(id, identifier, resourceType)