package ru.viscur.dh.fhir.model.enums

/**
 * Created at 03.10.2019 9:57 by SherbakovaMA
 *
 * Статус диагноза [ru.viscur.dh.fhir.model.entity.DiagnosticReport]
 */
enum class DiagnosticReportStatus {
    /**
     * Предварительный
     */
    preliminary,
    /**
     * Окончательный
     */
    final,
    /**
     * Ведущий синдром
     */
    mainSyndrome
}