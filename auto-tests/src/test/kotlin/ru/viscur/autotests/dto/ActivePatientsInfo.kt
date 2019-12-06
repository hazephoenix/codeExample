package ru.viscur.autotests.dto

import ru.viscur.dh.fhir.model.entity.Patient


data class ActivePatientsInfo(
        val patients: List<PatientInfo>
)

data class PatientInfo(
        val practitionerId: String,
        val patientId: String,
        val severity: String,
        val carePlanStatus: String,
        val queueOfficeId: String?,
        val patient: Patient
)