package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 01.10.2019 13:10 by SherbakovaMA
 *
 * Код
 *
 * @param code значение кода
 * @param display отображаемое описание
 * @param system система, в которой находится код
 */
class Coding @JsonCreator constructor(
        @JsonProperty("code") val code: String,
        @JsonProperty("display") val display: String? = null,
        @JsonProperty("system") val system: String? = null
)