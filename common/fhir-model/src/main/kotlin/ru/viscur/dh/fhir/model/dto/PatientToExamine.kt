package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

/**
 * Данные пациента, ожидающего осмотра ответственного врача
 *
 * @param practitionerId id ответственного мед. работника
 * @param patientId id пациента
 * @param severity степень тяжести
 * @param carePlanStatus статус маршрутного листа
 * @param queueOfficeId id офиса, в который пациент находится в очередь. если пациент не стоит в очереди, то пустое значение
 * @param patient пациент (все данные в ресурсе), [Patient]
 */
data class PatientToExamine(
        var practitionerId: String,
        var patientId: String,
        var severity: String,
        var carePlanStatus: CarePlanStatus,
        var queueOfficeId: String?,
        var patient: Patient
)