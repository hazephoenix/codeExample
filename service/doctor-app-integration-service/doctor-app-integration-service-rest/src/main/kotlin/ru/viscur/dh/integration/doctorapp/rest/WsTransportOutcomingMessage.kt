package ru.viscur.dh.integration.doctorapp.rest

import kotlin.reflect.KClass

class WsTransportOutcomingMessage(
        val status: Status,
        val payload: String?,
        val error: String?
) {


    enum class Status {
        Ok,
        Error
    }

    enum class PayloadType(val payloadClass: KClass<out Any>) {
        DoctorCall(ru.viscur.dh.integration.doctorapp.api.model.DoctorCall::class)
    }

}