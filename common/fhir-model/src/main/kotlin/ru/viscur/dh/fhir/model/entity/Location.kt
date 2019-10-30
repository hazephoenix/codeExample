package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 18:20 by SherbakovaMA
 *
 * Место (кабинет, отделение и т.д.)
 * [info](http://fhir-ru.github.io/location.html)
 *
 * @param name наименование
 * @param status статус
 * @param address адрес
 * @param type тип выполняемой процедуры/услуги
 */
class Location @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Location.id,
        @JsonProperty("name") var name: String,
        @JsonProperty("status") var status: LocationStatus = LocationStatus.BUSY,
        @JsonProperty("address") val address: Address? = null,
        @JsonProperty("type") val type: List<CodeableConcept>? = null,
        @JsonProperty("extension") var extension: LocationExtension? = null
) : BaseResource(id, identifier, resourceType)