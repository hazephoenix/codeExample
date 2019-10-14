package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.enums.EncounterLocationStatus

/**
 * Created at 02.10.2019 18:49 by SherbakovaMA
 *
 * Помещение во взаимодействии [ru.viscur.dh.fhir.model.entity.Encounter]
 *
 * @param location место проведения, ссылка на [Location]
 * @param status статус [EncounterLocationStatus]
 * @param physicalType тип места (отделение и т.д.) todo может не нужен
 * @param period период времени, в течение которого пациент присутствовал в этом месте
 */
class EncounterLocation @JsonCreator constructor(
        @JsonProperty("location") val location: Reference,
        @JsonProperty("status") val status: EncounterLocationStatus,
        @JsonProperty("physicalType") val physicalType: CodeableConcept? = null,
        @JsonProperty("period") val period: Period? = null
)