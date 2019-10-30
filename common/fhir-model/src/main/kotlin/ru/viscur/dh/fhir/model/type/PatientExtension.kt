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
// * @param citizenship гражданство
 */
class PatientExtension @JsonCreator constructor(
        @JsonProperty("nationality") var nationality: String,
        @JsonProperty("birthPlace") var birthPlace: Address,
        @JsonProperty("queueStatusUpdatedAt") var queueStatusUpdatedAt: Date? = now(),
        @JsonProperty("queueStatus") var queueStatus: PatientQueueStatus? = PatientQueueStatus.READY
//        ,
//        @JsonProperty("citizenship") val citizenship: String todo пока не нужно?
)