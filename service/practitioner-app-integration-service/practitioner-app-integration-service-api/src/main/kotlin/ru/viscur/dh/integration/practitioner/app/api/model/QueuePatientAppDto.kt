package ru.viscur.dh.integration.practitioner.app.api.model

import ru.viscur.dh.fhir.model.enums.Severity

class QueuePatientAppDto(
        val id: String,
        val orderInQueue: Int? = null,
        val severity: Severity,
        val code: String,
        val timeToProvideService: Int
)