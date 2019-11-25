package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.PractitionerExtension
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
 * @param extension доп. поля, [PractitionerExtension]
 */
class Practitioner @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Practitioner.id,
        @JsonProperty("name") var name: List<HumanName>,
        @JsonProperty("gender") var gender: Gender = Gender.unknown,
        @JsonProperty("qualification") var qualification: PractitionerQualification,
        @JsonProperty("extension") var extension: PractitionerExtension = PractitionerExtension()
) : BaseResource(id, identifier, resourceType)