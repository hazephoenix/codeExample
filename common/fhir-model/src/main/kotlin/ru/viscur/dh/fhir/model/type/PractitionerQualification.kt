package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 02.10.2019 18:04 by SherbakovaMA
 *
 * Квалификация мед. работника [ru.viscur.dh.fhir.model.entity.Practitioner]
 *
 * @param identifier id этой квалификации для мед. работника
 * @param code код квалификации
 * @param period период, в течение которого квалификация действительна
 */
class PractitionerQualification @JsonCreator constructor(
        @JsonProperty("identifier") val identifier: List<Identifier>? = null,
        @JsonProperty("code") val code: CodeableConcept,
        @JsonProperty("period") val period: Period
)