package ru.viscur.dh.datastorage.api.response

/**
 * Предполагаемый диагноз
 *
 * @param code Код предполагаемого диагноза
 * @param system Система кодирования (МКБ-10)
 * @param probability Вероятность в %
 */
data class PredictedDiagnosis(
        val code: String,
        val system: String,
        val probability: Double
)
