package ru.viscur.dh.integration.practitioner.app.api.cmd

import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.practitioner.call.model.CallGoal
import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory

class CreatePractitionerCallAppCmd {
    var practitionerId: String = ""
    var specializationCategory: CallableSpecializationCategory = CallableSpecializationCategory.Surgeon
    var goal: CallGoal = CallGoal.Consultation
    var locationId: String = ""
    val patientSeverity: Severity = Severity.GREEN
    var comment: String = ""
}