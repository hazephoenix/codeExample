package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 01.10.2019 13:10 by SherbakovaMA
 *
 * Код
 * Все указанные коды должны быть в ValueSet-ах
 *
 * @param code значение кода
 * @param system система, в которой находится код, "ValueSet/value_set_id"
 * @param display отображаемое описание
 */
class Coding @JsonCreator constructor(
        @JsonProperty("code") val code: String,
        @JsonProperty("system") val system: String,
        @JsonProperty("display") val display: String? = null
)