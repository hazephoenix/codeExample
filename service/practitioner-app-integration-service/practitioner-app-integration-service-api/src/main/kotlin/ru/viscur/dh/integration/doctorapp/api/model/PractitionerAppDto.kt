package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory


class PractitionerAppDto(
        id: String,
        fullName: String,
        val specializationCategories: List<CallableSpecializationCategory>,
        val disabled: Boolean
) : PersonAppDto(id, fullName)