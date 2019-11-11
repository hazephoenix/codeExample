package ru.viscur.dh.integration.doctorapp.api.cmd

import ru.viscur.dh.fhir.model.enums.Severity

class NewDoctorCallCmd {
    val doctorId: String = ""
    val whoCallsDoctorId: String = ""
    val locationId: String = ""
    val goal: CallGoal = CallGoal.Consultation
    val patientSeverity: Severity = Severity.GREEN


    enum class CallGoal {
        Emergency,
        Consultation
    }
}