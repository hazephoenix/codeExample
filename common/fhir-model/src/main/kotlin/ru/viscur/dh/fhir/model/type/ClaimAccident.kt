package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Created at 01.10.2019 14:46 by SherbakovaMA
 *
 * Детали несчастного случая
 * Место события заполняется одним полем из location*
 *
 * @param date когда инцидент случился
 * @param type тип несчастного случая
 * @param locationAddress адрес
 * @param locationReference ссылка на [ru.viscur.dh.fhir.model.entity.Location]
 */
class ClaimAccident @JsonCreator constructor(
        @JsonProperty("date") val date: Date,
        @JsonProperty("type") val type: CodeableConcept,
        @JsonProperty("locationAddress") val locationAddress: Address? = null,
        @JsonProperty("locationReference") val locationReference: Reference? = null
)