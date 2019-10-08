package ru.viscur.dh.paramedic.devicemonitor.dto

/**
 * Created at 30.09.2019 11:30 by TimochkinEA
 *
 * Результат обследования
 */
data class DiagnosticReport(
        override val resourceType: String = "DiagnosticReport",
        override val identifier: Identifier,
        val status: DiagnosticReportStatus = DiagnosticReportStatus.Registered,
        val code: String,
        val encounter: Reference<Encounter>,
        val result: List<Observation>
) : Resource
