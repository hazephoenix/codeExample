package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.PractitionerQualification
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 17:48 by SherbakovaMA
 *
 * Медицинский работник
 * [info](http://fhir-ru.github.io/practitioner.html)
 *
 * @param name информация о ФИО
 * @param gender пол
 * @param qualification квалификация специалиста, [PractitionerQualification]
 */
class Practitioner @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>,
        @JsonProperty("resourceType") resourceType: ResourceType = ResourceType.Practitioner,
        @JsonProperty("name") val name: List<HumanName>,
        @JsonProperty("gender") val gender: Gender = Gender.unknown,
        @JsonProperty("qualification") val qualification: PractitionerQualification
) : BaseResource(id, identifier, resourceType)