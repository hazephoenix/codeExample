package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 18:33 by SherbakovaMA
 *
 * Взаимодействие (случай обслуживания)
 * [info](http://fhir-ru.github.io/encounter.html)
 *
 * @param subject пациент, ссылка на [Patient]
 * @param diagnosis список диагнозов
 * @param hospitalization сведения о госпитализации в медицинском учреждении
 * @param location перечень помещений, в которых побывал пациент или переведен (в зависимости от типа)
 * @param extension доп. поля, [EncounterExtension]
 */
class Encounter @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType = ResourceType.Encounter,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("diagnosis") val diagnosis: List<EncounterDiagnosis>? = null,
        @JsonProperty("hospitalization") val hospitalization: EncounterHospitalization? = null,
        @JsonProperty("location") val location: List<EncounterLocation>? = null,
        @JsonProperty("extension") val extension: EncounterExtension? = null
) : BaseResource(id, identifier, resourceType)