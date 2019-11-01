package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Created at 01.10.2019 12:55 by SherbakovaMA
 *
 * Период
 *
 * @param start дата начала
 * @param end дата окончания
 */
class Period @JsonCreator constructor(
        @JsonProperty("start") val start: Date?,
        @JsonProperty("end") val end: Date? = null
)