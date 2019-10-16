package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.AddressType
import ru.viscur.dh.fhir.model.enums.AddressUse

/**
 * Created at 02.10.2019 19:13 by SherbakovaMA
 *
 * Адрес
 *
 * @param use назначение использования
 * @param type тип: физический, почтовый и т.д.
 * @param text текстовое представление адреса
 * @param line улица
 * @param city город, поселок и т.д.
 * @param district район
 * @param state область/край
 * @param postalCode почтовый код
 * @param country страна
 * @param period период времени, в течение которого адрес был/находится в использовании
 */
class Address @JsonCreator constructor(
        @JsonProperty("use") val use: AddressUse = AddressUse.home,
        @JsonProperty("type") val type: AddressType = AddressType.both,
        @JsonProperty("text") val text: String,
        @JsonProperty("line") val line: List<String>? = null,
        @JsonProperty("city") val city: String?,
        @JsonProperty("district") val district: String? = null,
        @JsonProperty("state") val state: String?,
        @JsonProperty("postalCode") val postalCode: String? = null,
        @JsonProperty("country") val country: String?,
        @JsonProperty("period") val period: Period? = null
)