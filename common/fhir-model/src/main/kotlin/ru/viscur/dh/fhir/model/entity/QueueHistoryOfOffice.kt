package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.Coding
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*
import java.util.*

/**
 * Created at 15.10.2019 9:12 by SherbakovaMA
 *
 * История статусов очереди по кабинетам
 * (Как менялись статусы каждого из кабинетов)
 */
class QueueHistoryOfOffice @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.QueueHistoryOfOffice.id,
        @JsonProperty("location") val location: Reference,
        @JsonProperty("status") val status: LocationStatus,
        @JsonProperty("fireDate") val fireDate: Date?,
        @JsonProperty("duration") var duration: Int?
) : BaseResource(id, identifier, resourceType)