package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 07.10.2019 13:57 by SherbakovaMA
 *
 * Направление на обследование
 * [info](http://fhir-ru.github.io/servicerequest.html)
 *
 * @param status статус
 * @param subject поциент, ссылка на [Patient]
 * @param performer список возможных исполнителей, ссылка на [Practitioner]
 * @param locationReference в каком кабинете предполагается проведение обследования (список всегда из одной записи), ссылка на [Location]
 * @param requester кто заказал процедуру, ссылка на [Practitioner]
 * @param code код процедуры/обследования
 * @param extension расширения [ServiceRequestExtension]
 */
class ServiceRequest @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.ServiceRequest.id,
        @JsonProperty("status") var status: ServiceRequestStatus = ServiceRequestStatus.active,
        @JsonProperty("subject") var subject: Reference? = null,
        @JsonProperty("performer") var performer: List<Reference>? = null,
        @JsonProperty("locationReference") var locationReference: List<Reference>? = null,
        @JsonProperty("requester") val requester: Reference? = null,
        @JsonProperty("code") val code: CodeableConcept,
        @JsonProperty("extension") var extension: ServiceRequestExtension? = null
) : BaseResource(id, identifier, resourceType) {
    constructor(code: String) : this(code = CodeableConcept(
            code = code,
            system = ValueSetName.OBSERVATION_TYPES
    ))
}