package ru.viscur.dh.integration.practitioner.app.api.event

class PractitionerCallAcceptedAppEvent(
        val callId: String,
        val timeToArrival: Short
)