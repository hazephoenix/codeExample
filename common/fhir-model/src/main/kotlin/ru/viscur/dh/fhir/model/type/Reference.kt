package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Created at 01.10.2019 13:50 by SherbakovaMA
 *
 * Ссылка на ресурс
 *
 * @param reference строковая ссылка на сущность. Абсолютная или относительная. Например, "patient/1d3ca"
 * @param type тип ресурса. Например, "Patient"
 * @param identifier идентификатор ресурса. заполняется, если [reference] не известен
 * @param display текстовое представление ресурса
 */
class Reference @JsonCreator constructor(
        @JsonProperty("reference") val reference: String? = null,
        @JsonProperty("type") val type: ResourceType.ResourceTypeId? = null,
        @JsonProperty("identifier") val identifier: Identifier? = null,
        @JsonProperty("display") val display: String? = null
) {
    constructor(res: BaseResource) : this(
            reference = "${res.resourceType}/${res.id}",
            type = res.resourceType
    )

    constructor(resourceType: ResourceType.ResourceTypeId, id: String) : this(
            reference = "${resourceType}/$id",
            type = resourceType
    )

    val id : String? = reference?.substringAfter("/")
}