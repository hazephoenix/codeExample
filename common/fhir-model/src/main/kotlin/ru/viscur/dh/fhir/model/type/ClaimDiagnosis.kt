package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 02.10.2019 12:16 by SherbakovaMA
 *
 * Диагноз обращения
 * Информация о болезни или проблеме определяется одним из diagnosis*
 *
 * @param sequence id диагноза
 * @param diagnosisCodeableConcept код диагноза
 * @param diagnosisReference диагноз в виде ссылки на [Condition]
 * @param type хронометраж или природа диагноза
 * @param onAdmission присутствовал ли при поступлении
 * @param packageCode код счета
 */
class ClaimDiagnosis @JsonCreator constructor(
        @JsonProperty("sequence") val sequence: Int? = null,
        @JsonProperty("diagnosisCodeableConcept") val diagnosisCodeableConcept: CodeableConcept? = null,
        @JsonProperty("diagnosisReference") val diagnosisReference: Reference? = null,
        @JsonProperty("type") val type: List<CodeableConcept>
//        , todo пока не используется
//        @JsonProperty("onAdmission") val onAdmission: CodeableConcept,
//        @JsonProperty("packageCode") val packageCode: CodeableConcept
)