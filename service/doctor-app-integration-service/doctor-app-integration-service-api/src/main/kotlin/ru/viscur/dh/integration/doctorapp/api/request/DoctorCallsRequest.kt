package ru.viscur.dh.integration.doctorapp.api.request

import java.util.*

class DoctorCallsRequest {
    /**
     * С каокго времени получить данные.
     * Если null, то все
     */
    val from: Date? = null

    /**
     * Типы вызовов
     */
    val types: Set<CallsTypes> = setOf()



    enum class CallsTypes {
        Incoming,
        Outcoming
    }
}