package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ValueSetStatus
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 03.10.2019 15:19 by SherbakovaMA
 *
 * Справочник кодов [Concept]
 * [info](http://fhir-ru.github.io/valueset.html)
 *
 * @param url однозначно идентифицирующая ссылка, уникальная, по ней ссылаются [Concept], например, "ValueSet/ICD-10"
 * @param name наименование (computer friendly)
 * @param title заголовок (human friendly)
 * @param description описание
 * @param status статус
 */
class ValueSet @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.ValueSet.id,
        @JsonProperty("url") val url: String,
        @JsonProperty("name") val name: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("description") val description: String? = null,
        @JsonProperty("status") val status: ValueSetStatus = ValueSetStatus.active
) : BaseResource(id, identifier, resourceType)