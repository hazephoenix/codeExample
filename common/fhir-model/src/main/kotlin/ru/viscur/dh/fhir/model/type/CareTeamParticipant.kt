package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 03.10.2019 8:38 by SherbakovaMA
 *
 * Член команды [ru.viscur.dh.fhir.model.entity.CareTeam]
 *
 * @param role вид участия
 * @param member кто участвует, ссылка на [ru.viscur.dh.fhir.model.entity.Practitioner], [ru.viscur.dh.fhir.model.entity.Organization], [ru.viscur.dh.fhir.model.entity.CareTeam] и т.д.
 * @param period временной период участника
 */
class CareTeamParticipant @JsonCreator constructor(
        @JsonProperty("role") val role: List<CodeableConcept>? = null,
        @JsonProperty("member") val member: Reference,
        @JsonProperty("period") val period: Period? = null
)