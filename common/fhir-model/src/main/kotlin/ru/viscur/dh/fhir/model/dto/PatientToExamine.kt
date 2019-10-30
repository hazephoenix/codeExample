package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*

/**
 * Данные пациента, ожидающего осмотра ответственного врача
 */
data class PatientToExamine(
        var patient: Patient? = null,
        var carePlanStatus: CarePlanStatus? = null,
        var patientId: String? = null,
        var severity: Coding? = null
)