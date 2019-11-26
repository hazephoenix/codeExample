package ru.viscur.dh.integration.doctorapp.api.cmd

import ru.viscur.dh.datastorage.api.model.call.CallGoal
import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization
import ru.viscur.dh.fhir.model.enums.Severity

class NewDoctorCallCmd {
    var doctorId: String = ""
    var  specialization: CallableSpecialization = CallableSpecialization.Surgeon
    var goal: CallGoal = CallGoal.Consultation
    var locationId: String = ""
    val patientSeverity: Severity = Severity.GREEN
    var comment: String = ""


}