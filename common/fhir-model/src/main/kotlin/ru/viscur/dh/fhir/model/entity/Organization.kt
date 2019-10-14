package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Address
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 03.10.2019 9:22 by SherbakovaMA
 *
 * Организация
 * [info](http://fhir-ru.github.io/organization.html)
 *
 * @param name наименование
 * @param address адрес (или несколько)
 */
class Organization @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType = ResourceType.Organization,
        @JsonProperty("name") val name: String,
        @JsonProperty("address") val address: List<Address>? = null
) : BaseResource(id, identifier, resourceType)