package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp
import java.util.*

/**
 * Created at 02.10.2019 12:47 by SherbakovaMA
 *
 * Один из разрешенных ответов в пункте [QuestionnaireItem] вопросника [ru.viscur.dh.fhir.model.entity.Questionnaire]
 * Заполняется один из value*
 *
 * @param initialSelected выбран по умолчанию
 */
class QuestionnaireItemAnswerOption @JsonCreator constructor(
        @JsonProperty("valueInteger") val valueInteger: Int? = null,
        @JsonProperty("valueDate") val valueDate: Date? = null,
        @JsonProperty("valueTime") val valueTime: Timestamp? = null,
        @JsonProperty("valueString") val valueString: String? = null,
        @JsonProperty("valueCoding") val valueCoding: Coding? = null,
        @JsonProperty("valueRegerence") val valueRegerence: Reference? = null,
        @JsonProperty("initialSelected") val initialSelected: Boolean = false
)