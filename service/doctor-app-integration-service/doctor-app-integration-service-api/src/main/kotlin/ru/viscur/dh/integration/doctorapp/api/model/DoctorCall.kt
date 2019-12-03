package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.datastorage.api.model.call.CallGoal
import ru.viscur.dh.datastorage.api.model.call.CallStatus
import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization
import ru.viscur.dh.fhir.model.enums.Severity
import java.util.*

class DoctorCall(
        val id: String,
        val dateTime: Date,
        val caller: Person,
        val specialization: CallableSpecialization,
        val doctor: CallableDoctor,
        val goal: CallGoal,
        val patientSeverity: Severity,
        val location: Location,
        val comment: String,
        val status: CallStatus,
        val timeToArrival: Short?
)