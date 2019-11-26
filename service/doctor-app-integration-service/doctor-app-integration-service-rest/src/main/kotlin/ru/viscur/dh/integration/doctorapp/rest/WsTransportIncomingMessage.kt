package ru.viscur.dh.integration.doctorapp.rest

import kotlin.reflect.KClass

/**
 * Транспортное сообщение для обмена данными по WebSocket
 */
class WsTransportIncomingMessage(
        /**
         * Тип полезной нагрузки
         */
        val payloadType: PayloadType = PayloadType.Unknown,

        /**
         * Текст JSON. Десериализуется в [PayloadType.payloadClass]
         */
        val payload: String = ""
) {


    /**
     * Поддерживаемые типы сообщений
     */
    enum class PayloadType(val payloadClass: KClass<out Any>) {
        Unknown(Unit::class),
        NewDoctorCallCmd(NewDoctorCallCmd::class)
    }
}