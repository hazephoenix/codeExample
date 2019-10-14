package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.HumanNameUse

/**
 * Created at 02.10.2019 17:58 by SherbakovaMA
 *
 * Описание имени человека
 *
 * @param use тип: официальное, устаревшее и т.д., [HumanNameUse]
 * @param text представление полного ФИО
 * @param family фамилия
 * @param given набор имен (имя, отчество)
 * @param period период, когда это описание имени было катуально
//todo еще есть suffix, prefix
 */
class HumanName @JsonCreator constructor(
        @JsonProperty("use") val use: HumanNameUse = HumanNameUse.official,
        @JsonProperty("text") val text: String,
        @JsonProperty("family") val family: String,
        @JsonProperty("given") val given: List<String>,
        @JsonProperty("period") val period: Period? = null
)