package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 03.10.2019 17:35 by SherbakovaMA
 *
 * Назначенный специалист в обращении [ru.viscur.dh.fhir.model.entity.Claim]
 *
 * @param responsible ответственный в текущем списке?
 * @param provider специалист, ссылка на [ru.viscur.dh.fhir.model.entity.Practitioner]/[ru.viscur.dh.fhir.model.entity.PractitionerRole]
 * @param role специалист с какой ролью в текущей команде
 * @param qualification квалификация
 */
class ClaimCareTeam @JsonCreator constructor(
        @JsonProperty("responsible") val responsible: Boolean = false,
        @JsonProperty("provider") val provider: Reference,
        @JsonProperty("role") val role: CodeableConcept? = null,
        @JsonProperty("qualification") val qualification: CodeableConcept? = null
)