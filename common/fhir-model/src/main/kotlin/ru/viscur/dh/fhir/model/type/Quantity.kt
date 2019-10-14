package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.QuantityComparator

/**
 * Created at 01.10.2019 14:12 by SherbakovaMA
 *
 * Количество
 *
 * @param value числовое значение
 * @param comparator как понимать значение. Если не указан, то '='
 * @param system система, в которой находится указанный код единиц измерения
 * @param code код единицы измерения
 * @param unit отображаемая единица измерения в текстовом формате (если ее кода нет в системе кодов)
 */
class Quantity @JsonCreator constructor(
        @JsonProperty("value") val value: Double,
        @JsonProperty("comparator") val comparator: QuantityComparator? = null,
        @JsonProperty("system") val system: String? = null,
        @JsonProperty("code") val code: String? = null,
        @JsonProperty("unit") val unit: String? = null
)