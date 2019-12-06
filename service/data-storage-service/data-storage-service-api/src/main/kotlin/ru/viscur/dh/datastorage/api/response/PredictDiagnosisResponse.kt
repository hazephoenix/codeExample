package ru.viscur.dh.datastorage.api.response

/**
 * Данные, возвращаемые на запрос предположения диагноза [ru.viscur.dh.datastorage.api.util.PatientClassifier]
 *
 * @param diagnoses Список предполагаемых диагнозов
 */
data class PredictDiagnosisResponse(
        val diagnoses: List<PredictedDiagnosis>
)