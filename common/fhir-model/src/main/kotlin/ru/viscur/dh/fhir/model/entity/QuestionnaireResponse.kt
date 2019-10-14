package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.QuestionnaireResponseStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.QuestionnaireResponseItem
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 16:03 by SherbakovaMA
 *
 * Ответ на опросник (опросник с ответами)
 * [info](http://fhir-ru.github.io/questionnaireresponse.html)
 *
 * @param status статус, [QuestionnaireResponseStatus]
 * @param author мед. работник, который внес ответы, ссылка на [Practitioner]
 * @param source пациент, чьи данные внесены, ссылка на [Patient]
 * @param questionnaire строковая ссылка на опросник [Questionnaire] в примерном формате "Questionnaire/gcs"
 * @param item список пунктов с ответами, [QuestionnaireResponseItem]
 */
class QuestionnaireResponse @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.QuestionnaireResponse.id,
        @JsonProperty("status") val status: QuestionnaireResponseStatus,
        @JsonProperty("author") val author: Reference,
        @JsonProperty("source") val source: Reference,
        @JsonProperty("questionnaire") val questionnaire: String,
        @JsonProperty("item") val item: List<QuestionnaireResponseItem>
) : BaseResource(id, identifier, resourceType)