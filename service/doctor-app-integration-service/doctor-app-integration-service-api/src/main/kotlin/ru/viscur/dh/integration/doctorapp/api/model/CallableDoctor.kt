package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.datastorage.api.model.call.CallableSpecializationCategory

class CallableDoctor(
        id: String,
        fullName: String,
        val specializationCategories: List<CallableSpecializationCategory>,
        val disabled: Boolean
) : Person(id, fullName)