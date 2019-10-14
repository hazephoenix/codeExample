package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 02.10.2019 18:39 by SherbakovaMA
 *
 * Диагноз, относящийся к взаимодействию [Encounter]
 *
 *
 * @param condition состояние пациента: диагноз или процедура, относящийся к этому диагнозу взаимодействия, ссылка на [Condition] или [Procedure]
 * @param use роль диагноза во взамиодействии (подтвержден, отменен и т.д.)
 * @param rank место (№п/п) в списке диагнозов взаимодействия. Первый идет основной, затем остальные
 */
class EncounterDiagnosis @JsonCreator constructor(
        @JsonProperty("condition") val condition: Reference,
        @JsonProperty("use") val use: CodeableConcept? = null,
        @JsonProperty("rank") val rank: Int? = null
)