package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ListResourceMode
import ru.viscur.dh.fhir.model.enums.ListResourceStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.ListResourceEntry
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 10.10.2019 15:06 by SherbakovaMA
 *
 * Список ссылок на ресурс
 * [info](http://fhir-ru.github.io/list.html)
 *
 * @param status статус
 * @param mode тип
 * @param entry содержимое (элементы списка), [ListResourceEntry]
 * @param title описательное название
 */
class ListResource @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.ListResource.id,
        @JsonProperty("status") val status: ListResourceStatus = ListResourceStatus.current,
        @JsonProperty("mode") val mode: ListResourceMode = ListResourceMode.working,
        @JsonProperty("entry") val entry: List<ListResourceEntry>,
        @JsonProperty("title") val title: String? = null
) : BaseResource(id, identifier, resourceType)