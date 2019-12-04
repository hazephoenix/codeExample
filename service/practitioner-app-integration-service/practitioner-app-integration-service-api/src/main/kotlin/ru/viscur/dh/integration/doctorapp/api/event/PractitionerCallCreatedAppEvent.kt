package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.DoctorCallAppDto

class PractitionerCallCreatedAppEvent(
        val call: DoctorCallAppDto
)