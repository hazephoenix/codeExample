package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 02.10.2019 17:35 by SherbakovaMA
 *
 * Пункт ответа в ответе на опросник [ru.viscur.dh.fhir.model.entity.QuestionnaireResponse]
 *
 * @param linkId на какой пункт опросника этот пункт ответа. указывает на [QuestionnaireItem.linkId]
 * @param text название группы или текст вопроса
 * @param answer ответы на вопрос, [QuestionnaireResponseItemAnswer]
 * @param item вложенные пунткы ответов
 */
class QuestionnaireResponseItem @JsonCreator constructor(
        @JsonProperty("linkId") val linkId: String,
        @JsonProperty("text") val text: String? = null,
        @JsonProperty("answer") var answer: List<QuestionnaireResponseItemAnswer>,
        @JsonProperty("item") val item: List<QuestionnaireResponseItem>? = null
)