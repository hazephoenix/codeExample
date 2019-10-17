package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import java.util.*

/**
 * Created at 15.10.2019 10:02 by SherbakovaMA
 *
 * Доп поля [местоположения Location][ru.viscur.dh.fhir.model.entity.Location]
 *
 * @param statusUpdatedAt когда поменялся [статус Location.status][ru.viscur.dh.fhir.model.entity.Location.status]
 * @param lastPatientInfo информация кабинета о последнем принятом пациенте, [LocationExtensionLastPatientInfo]
 */
class LocationExtension @JsonCreator constructor(
        @JsonProperty("statusUpdatedAt") var statusUpdatedAt: Date? = null,
        @JsonProperty("lastPatientInfo") var lastPatientInfo: LocationExtensionLastPatientInfo? = null
)