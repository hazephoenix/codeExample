package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.enums.*

/**
 * Степень тяжести пациента
 *
 * @param severity Степень тяжести [Severity]
 * @param reason Причина распределения в данный поток
 */
data class PatientSeverity(
        var severity: Severity,
        var reason: String
)