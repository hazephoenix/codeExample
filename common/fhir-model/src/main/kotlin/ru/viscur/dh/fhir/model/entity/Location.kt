package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.valueSets.IdentifierType

/**
 * Created at 02.10.2019 18:20 by SherbakovaMA
 *
 * Место (кабинет, отделение и т.д.)
 * [info](http://fhir-ru.github.io/location.html)
 *
 * @param name наименование
 * @param status статус
 * @param address адрес
 * @param type тип места, коды в [ru.viscur.dh.fhir.model.valueSets.ValueSetName.LOCATION_TYPE]
 * @param extension доп. поля, [LocationExtension]
 */
class Location @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Location.id,
        @JsonProperty("name") var name: String,
        @JsonProperty("status") var status: LocationStatus = LocationStatus.BUSY,
        @JsonProperty("address") val address: Address? = null,
        @JsonProperty("type") val type: List<CodeableConcept>,
        @JsonProperty("extension") var extension: LocationExtension
) : BaseResource(id, identifier, resourceType) {

    /**
     * Тип места
     */
    fun type() = type.first().code()

    /**
     * Номер кабинета
     */
    fun officeNumber(): String? {
        val officeNumberIdentifier =
                identifier?.filter {
                    it.type.coding.any { coding ->
                        coding.code == IdentifierType.OFFICE_NUMBER.name
                    }
                }?.firstOrNull()
        return officeNumberIdentifier?.value;
    }
}