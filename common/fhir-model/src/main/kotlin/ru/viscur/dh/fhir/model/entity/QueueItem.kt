package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 14.10.2019 16:24 by SherbakovaMA
 *
 * Элемент очереди в кабинеты (описание пациента в очереди в кабинет)
 *
 * @param status статус пациента в очереди, [PatientQueueStatus]
 * @param subject пациент
 * @param location кабинет
 * @param estDuration предположительная продолжительность осмотра, сек
 * @param queueCode код в очереди
 * @param onum порядковый номер в очередь (в определенный кабинет)
 */
class QueueItem @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.QueueItem.id,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("location") val location: Reference,
        @JsonProperty("estDuration") val estDuration: Int,
        @JsonProperty("queueCode") val queueCode: String,
        @JsonProperty("onum") var onum: Int? = null
) : BaseResource(id, identifier, resourceType) {
    /**
     * Вычисляются, не хранятся в объекте, т к меняется у пациента. Всегда читаются актуальные данные
     */
    @JsonIgnore var severity: Severity? = null
    @JsonIgnore var patientQueueStatus: PatientQueueStatus? = null

    override fun toString(): String {
        return """  QueueItem($onum, $estDuration, $patientQueueStatus, $severity, patientId=${subject.id}, officeId=${location.id})""".trimMargin()
    }
}
