package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet

enum class PacketType(val header: ByteArray) {
    /**
     * Запрос на аутентификацию (он же является Hello запросом)
     */
    AuthRequest(byteArrayOf(0x01, 0x01)),

    /**
     * KeepAlive
     */
    KeepAlive(byteArrayOf(0x01, 0x02)),

    /**
     * Ответ на атворизацию
     */
    AuthResponse(byteArrayOf(0x02, 0x01)),

    /**
     * Push сообщение
     */
    PushMessage(byteArrayOf(0x02, 0x02))
}