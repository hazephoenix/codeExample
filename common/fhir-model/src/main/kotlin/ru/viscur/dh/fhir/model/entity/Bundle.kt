package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.valueSets.BundleType

/**
 * Контейнер для коллекции ресурсов
 * [info](http://fhir-ru.github.io/bundle.html)
 *
 * @param resourceType Тип ресурса
 * @param type Тип контейнера [BundleType]
 * @param entry Список ресурсов [BundleEntry]
 */
class Bundle @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Bundle.id,
        @JsonProperty("type") var type: String = BundleType.BATCH.value,
        @JsonProperty("entry") val entry: List<BundleEntry>
) : BaseResource(id, identifier, resourceType)