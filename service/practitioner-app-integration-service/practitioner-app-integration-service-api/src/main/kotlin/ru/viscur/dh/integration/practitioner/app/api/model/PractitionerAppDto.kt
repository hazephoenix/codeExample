package ru.viscur.dh.integration.practitioner.app.api.model

import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory


class PractitionerAppDto(
        id: String,
        fullName: String,
        val specializationCategories: List<CallableSpecializationCategory>,
        val disabled: Boolean
) : PersonAppDto(id, fullName)