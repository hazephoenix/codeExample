package ru.viscur.dh.fhir.model.enums

/**
 * Статус клинической оценки состояния больного
 */
enum class ClinicalImpressionStatus {
    /**
     * Активный/текущий. Пока пациент проходит все этапы приемного отделения
     */
    active,
    /**
     * Завершенный
     */
    completed,
    /**
     * Отменен
     */
    cancelled
}