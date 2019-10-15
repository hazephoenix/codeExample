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
import java.sql.Timestamp
import java.util.*

/**
 * Created at 15.10.2019 9:12 by SherbakovaMA
 *
 * История (статистика) прохождения пациента в очереди
 */
class QueueHistoryOfOffice @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.QueueHistoryOfOffice.id,
        @JsonProperty("subject") val subject: Reference? = null,
        @JsonProperty("location") val location: Reference,
        @JsonProperty("status") val status: LocationStatus,
        @JsonProperty("fireDate") val fireDate: Date?,
        @JsonProperty("duration") var duration: Int?,
        @JsonProperty("severity") var severity: Severity? = null,
        @JsonProperty("diagnosticConclusion") var diagnosticConclusion: String? = null,
        @JsonProperty("ageGroup") var ageGroup: Int? = null
) : BaseResource(id, identifier, resourceType)