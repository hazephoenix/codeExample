package ru.viscur.dh.fhir.model.enums

/**
 * Статус клинической оценки состояния больного
 */
enum class ClinicalImpressionStatus {
    draft,
    /**
     * Активный/текущий. Пока пациент проходит все этапы приемного отделения
     */
    active,
    /**
     * Завершенный
     */
    completed,
    entered_in_error,
    /**
     * Отменен
     */
    cancelled
}