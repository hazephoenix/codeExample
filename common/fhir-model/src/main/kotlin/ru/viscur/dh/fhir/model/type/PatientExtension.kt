package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.utils.now
import java.util.*

/**
 * Created at 03.10.2019 8:29 by SherbakovaMA
 *
 * Доп. поля [пациента Patient][ru.viscur.dh.fhir.model.entity.Patient]
 *
 * @param nationality национальность
 * @param birthPlace место рождения
 * @param queueStatus статус в очереди, [PatientQueueStatus]
 * @param queueStatusUpdatedAt дата-время изменения статуса в очереди [queueStatus]
 */
class PatientExtension @JsonCreator constructor(
        @JsonProperty("nationality") var nationality: String,
        @JsonProperty("birthPlace") var birthPlace: Address,
        @JsonProperty("queueStatus") var queueStatus: PatientQueueStatus? = PatientQueueStatus.READY,
        @JsonProperty("queueStatusUpdatedAt") var queueStatusUpdatedAt: Date? = now()
)