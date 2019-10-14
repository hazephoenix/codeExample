package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.QuestionnaireItemType
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 12:39 by SherbakovaMA
 *
 * Вопросы и разделы вопросника [Questionnaire] (один пункт в вопроснике)
 *
 * @param linkId уникальный id для записи (уникальный идентификатор вопроса в опроснике)
 * @param text основной текст пункта/вопроса
 * @param type тип пункта, [QuestionnaireItemType]
 * @param prefix префикс пункта, например, "1(а)", "2.3.4"
 * @param required обязатеьлность
 * @param maxLength максимальная длина ответа
 * @param answerOption возможные ответы, список [QuestionnaireItemAnswerOption]
 * @param item вложенные пункты, список [QuestionnaireItem]
 */
class QuestionnaireItem @JsonCreator constructor(
        @JsonProperty("linkId") val linkId: String = genId(),
        @JsonProperty("text") val text: String,
        @JsonProperty("type") val type: QuestionnaireItemType,
        @JsonProperty("prefix") val prefix: String? = null,
        @JsonProperty("required") val required: Boolean = false,
        @JsonProperty("maxLength") val maxLength: Int? = null,
        @JsonProperty("answerOption") val answerOption: List<QuestionnaireItemAnswerOption>? = null,
        @JsonProperty("item") val item: List<QuestionnaireItem>? = null
)