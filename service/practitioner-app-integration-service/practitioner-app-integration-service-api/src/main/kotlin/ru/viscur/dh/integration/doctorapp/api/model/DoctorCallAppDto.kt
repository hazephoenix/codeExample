package ru.viscur.dh.integration.doctorapp.api.model


import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.practitioner.call.model.CallGoal
import ru.viscur.dh.practitioner.call.model.CallStatus
import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory
import java.util.*

class DoctorCallAppDto(
        val id: String,
        val dateTime: Date,
        val caller: PersonAppDto,
        val specializationCategory: CallableSpecializationCategory,
        val doctor: PractitionerAppDto,
        val goal: CallGoal,
        val patientSeverity: Severity,
        val location: LocationAppDto,
        val comment: String,
        val status: CallStatus,
        val timeToArrival: Short?
)