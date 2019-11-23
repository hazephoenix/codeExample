package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ClaimStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 01.10.2019 14:23 by SherbakovaMA
 *
 * Обращение пациента
 * [info](http://fhir-ru.github.io/claim.html)
 *
 * @param identifier в поле value номер обращения
 * @param patient ссылка на пациента [Patient]
 * @param status статус, [ClaimStatus]
 * @param supportingInfo доп.информация:  String/Boolean/Other, [ClaimSupportingInfo]
 * @param accident детали происшествия, [ClaimAccident]
 */
class Claim @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Claim.id,
        @JsonProperty("patient") var patient: Reference,
        @JsonProperty("status") var status: ClaimStatus = ClaimStatus.active,
        @JsonProperty("supportingInfo") val supportingInfo: List<ClaimSupportingInfo>? = null,
        @JsonProperty("accident") val accident: ClaimAccident
) : BaseResource(id, identifier, resourceType)