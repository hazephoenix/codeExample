package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.QuestionnaireItem
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 12:32 by SherbakovaMA
 *
 * Опросник
 * [info](http://fhir-ru.github.io/questionnaire.html)
 *
 * @param name название в системе. Например, "paramedic-qa-form"
 * @param title заголовок. Например, "Сортировочная карта", "Характер травм"
 * @param item вопросы/разделы
 */
class Questionnaire @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Questionnaire.id,
        @JsonProperty("name") val name: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("item") val item: List<QuestionnaireItem>
) : BaseResource(id, identifier, resourceType)