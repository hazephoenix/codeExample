package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.enums.*

/**
 * Степень тяжести пациента
 *
 * @param color Цвет сортировочного потока [PatientSeverityColor]
 * @param reason Причина распределения в данный поток
 */
data class PatientSeverity(
        var color: PatientSeverityColor,
        var reason: String
)