package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.fhir.model.enums.Severity

class DoctorCall(
        val from: String,
        val to: String,
        val location: String,
        val patientSeverity: Severity
) {


}