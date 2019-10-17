package ru.viscur.dh.fhir.model.enums

/**
 * Статус клинической оценки состояния больного
 */
enum class ClinicalImpressionStatus {
    draft,
    active,
    completed,
    entered_in_error
}