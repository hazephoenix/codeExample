package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.utils.now
import java.util.*
import java.util.*

/**
 * Created at 15.10.2019 9:12 by SherbakovaMA
 *
 * История (статистика) прохождения пациента в очереди
 */
class QueueHistoryOfPatient @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.QueueHistoryOfPatient.id,
        @JsonProperty("status") val status: PatientQueueStatus,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("location") val location: Reference? = null,
        @JsonProperty("fireDate") val fireDate: Date,
        @JsonProperty("duration") var duration: Int
) : BaseResource(id, identifier, resourceType)