package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.PatientExtension
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.utils.toAge
import java.util.*

/**
 * Created at 01.10.2019 8:44 by SherbakovaMA
 *
 * Пациент
 * [info](http://fhir-ru.github.io/patient.html)
 *
 * @param name ФИО
 * @param birthDate дата рождения
 * @param gender пол
 * @param extension доп. поля, [PatientExtension]
 */
class Patient @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>?,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Patient.id,
        @JsonProperty("name") val name: List<HumanName>,
        @JsonProperty("birthDate") val birthDate: Date,
        @JsonProperty("gender") val gender: Gender,
        @JsonProperty("extension") val extension: PatientExtension,
        @JsonProperty("age") val age: Int = birthDate.toAge()
) : BaseResource(id, identifier, resourceType)