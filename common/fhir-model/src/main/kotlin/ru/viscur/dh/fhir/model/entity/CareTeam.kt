package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CareTeamParticipant
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 01.10.2019 13:04 by SherbakovaMA
 *
 * Команда (группа/бригада) мед. персонала и организаций, участвующие в оказании помощи пациенту
 * [info](http://fhir-ru.github.io/careteam.html)
 *
 * @param category категория (код)
 * @param name наименование
 * @param participant члены команды/ответственный
 * @param subject для кого предназначена эта команда по уходу, ссылка на [Patient]
 * @param note можно вписать ФИО ответсвенного сюда, тк. его может не быть в нашей системе
 */
class CareTeam @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType = ResourceType.CareTeam,
        @JsonProperty("category") val category: List<CodeableConcept>,
        @JsonProperty("name") val name: String,
        @JsonProperty("participant") val participant: List<CareTeamParticipant>,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("note") val note: List<Annotation>? = null
) : BaseResource(id, identifier, resourceType)