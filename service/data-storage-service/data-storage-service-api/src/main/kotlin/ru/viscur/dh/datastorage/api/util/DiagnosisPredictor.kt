package ru.viscur.dh.datastorage.api.util

import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.fhir.model.entity.*

interface DiagnosisPredictor {
    /**
     * Подсказать диагноз
     */
    fun predict(bundle: Bundle): PredictDiagnosisResponse

    /**
     * Сохранить данные для обучения
     */
    fun saveTrainingSample(diagnosticReport: DiagnosticReport)
}