package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.PractitionerRoleAvailableTime
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 02.10.2019 18:09 by SherbakovaMA
 *
 * Роли определеннного медицинского работника
 * [info](http://fhir-ru.github.io/practitionerrole.html)
 *
 * @param practitioner ссылка на мед. работника [Practitioner]
 * @param code коды ролей, которые может выполнять мед. работник
 * @param speciality специальность (или несколько)
 * @param location кабинет/отделение (или несколько), в котором предоставляет услуги, ссылка на [Location]
 * @param availableTime расписание приема
 */
class PractitionerRole @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.PractitionerRole.id,
        @JsonProperty("practitioner") val practitioner: Reference,
        @JsonProperty("code") val code: List<CodeableConcept>,
        @JsonProperty("speciality") val speciality: List<CodeableConcept>,
        @JsonProperty("location") val location: List<Reference>,
        @JsonProperty("availableTime") val availableTime: List<PractitionerRoleAvailableTime>? = null
) : BaseResource(id, identifier, resourceType)