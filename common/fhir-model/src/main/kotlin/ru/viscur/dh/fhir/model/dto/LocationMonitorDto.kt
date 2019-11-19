package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import java.util.*

/**
 * Created at 15.11.2019 9:06 by SherbakovaMA
 *
 * Информация для монитора для отображения очереди/приема в кабинет
 *
 * @param locationMonitorId id места/монитора
 * @param officeId id кабинета
 * @param officeStatus статус кабинета, соответствует [ru.viscur.dh.fhir.model.enums.LocationStatus]
 * @param locationType тип места (смотровая, кабинет, кр/ж/з зона)
 * @param items элементы очереди, [LocationMonitorNextOfficeForPatientInfoDto]
 * @param nextOfficeForPatientsInfo информация о последних принятых пациентах. [LocationMonitorNextOfficeForPatientInfoDto]
 * @param fireDate время формирования информации
 */
data class LocationMonitorDto(
        val locationMonitorId: String,
        val officeId: String,
        val officeStatus: String,
        val locationType: String,
        val items: List<LocationMonitorQueueItemDto>,
        val nextOfficeForPatientsInfo: List<LocationMonitorNextOfficeForPatientInfoDto>,
        val fireDate: Date
)

/**
 * Created at 15.11.2019 9:06 by SherbakovaMA
 *
 * Описание элемента очереди в [LocationMonitorDto]
 *
 * @param onum №п/п
 * @param patientId id пациента
 * @param status статус элемента в очереди, соответствует [PatientQueueStatus]
 * @param severity степень тяжести, соответствует [ru.viscur.dh.fhir.model.enums.Severity]
 * @param queueNumber код в очереди (З-012, Ж-113...)
 */
data class LocationMonitorQueueItemDto(
        val onum: Int,
        val patientId: String,
        val status: String,
        val severity: String,
        val queueNumber: String
)

/**
 * Created at 15.11.2019 9:06 by SherbakovaMA
 *
 * Описание последнего принятого пациента в [LocationMonitorDto]
 *
 * @param patientId id пациента
 * @param severity степень тяжести, соответствует [ru.viscur.dh.fhir.model.enums.Severity]
 * @param queueNumber код в очереди (З-012, Ж-113...)
 * @param nextOfficeId id следующего кабинета
 */
data class LocationMonitorNextOfficeForPatientInfoDto(
        val patientId: String,
        val severity: String,
        val queueNumber: String,
        val nextOfficeId: String
)