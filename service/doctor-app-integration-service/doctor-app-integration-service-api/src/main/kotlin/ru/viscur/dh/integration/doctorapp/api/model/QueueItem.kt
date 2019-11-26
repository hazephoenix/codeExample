package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.fhir.model.enums.Severity

class QueuePatient(
        val id: String,
        val orderInQueue: Int,
        val severity: Severity,
        val code: String,
        val timeToProvideService: Long
)