package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

/**
 * Created at 01.10.2019 13:17 by SherbakovaMA
 *
 * Аннотация
 *
 * @param text значение
 * @param time когда аннотация была сделана
 */
class Annotation @JsonCreator constructor(
        @JsonProperty("text") val text: String,
        @JsonProperty("time") val time: Timestamp? = null
)