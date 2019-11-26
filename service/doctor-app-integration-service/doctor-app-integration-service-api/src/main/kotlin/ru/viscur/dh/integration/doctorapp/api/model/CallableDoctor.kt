package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization

class CallableDoctor(
        id: String,
        fullName: String,
        val specializations: List<CallableSpecialization>,
        val disabled: Boolean
) : Person(id, fullName)