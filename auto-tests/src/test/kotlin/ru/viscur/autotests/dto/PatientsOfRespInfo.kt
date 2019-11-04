package ru.viscur.autotests.dto

import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Created at 04.11.2019 14:56 by SherbakovaMA
 *
 * Информация о пациентах в отвественности врача
 *
 * @param practitionerId id врача
 */
data class PatientsOfRespInfo(
        val practitionerId: String,
        val patientsInfo: List<PatientOfRespInfo>
)

/**
 * Информация о пациенте в отвественности врача
 */
data class PatientOfRespInfo(
        val patientId: String,
        val severity: Severity
)