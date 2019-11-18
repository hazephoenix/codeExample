package ru.viscur.dh.datastorage.api.response

/**
 * Данные, возвращаемые на запрос предположения диагноза [ru.viscur.dh.datastorage.api.util.DiagnosisPredictor]
 *
 * @param diagnoses Список предполагаемых диагнозов
 */
data class PredictDiagnosisResponse(
        val diagnoses: List<PredictedDiagnosis>
)

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
