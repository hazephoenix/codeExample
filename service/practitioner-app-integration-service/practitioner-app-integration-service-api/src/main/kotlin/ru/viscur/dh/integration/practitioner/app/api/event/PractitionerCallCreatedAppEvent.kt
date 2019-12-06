package ru.viscur.dh.integration.practitioner.app.api.event

import ru.viscur.dh.integration.practitioner.app.api.model.PractitionerCallAppDto

class PractitionerCallCreatedAppEvent(
        val call: PractitionerCallAppDto
)