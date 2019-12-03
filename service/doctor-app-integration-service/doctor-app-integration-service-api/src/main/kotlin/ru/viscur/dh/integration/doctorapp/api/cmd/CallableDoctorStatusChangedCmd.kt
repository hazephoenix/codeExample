package ru.viscur.dh.integration.doctorapp.api.cmd

class CallableDoctorStatusChangedCmd(
        val doctorId: String,
        val status: Status
) {


    enum class Status {
        Enabled,
        Disabled
    }


}