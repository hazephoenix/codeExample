package ru.viscur.dh.datastorage.api.util

import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.fhir.model.entity.*

interface DiagnosisPredictor {
    /**
     * Определить список предположительных диагнозов
     *
     * @param bundle Данные пациента, присылаемые фельдшером
     * @param take Сколько диагнозов
     * @return [PredictDiagnosisResponse] Список предполагаемых диагнозов
     */
    fun predict(bundle: Bundle, take: Int): PredictDiagnosisResponse

    /**
     * Сохранить данные для обучения
     *
     * @param diagnosticReport Окончательный диагноз пациента
     * @return id созданного TrainingSample
     */
    fun saveTrainingSample(diagnosticReport: DiagnosticReport): Long?
}