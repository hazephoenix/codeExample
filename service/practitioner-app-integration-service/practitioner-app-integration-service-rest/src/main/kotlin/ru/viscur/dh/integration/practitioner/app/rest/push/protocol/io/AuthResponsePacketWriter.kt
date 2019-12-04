package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io


import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.AuthResponsePacket
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import java.io.DataOutputStream

/**
 * Writer пакет ответа на запрос аутентификации
 *
 * Формат сообщения:
 *  * {Header: 2 byte}
 *  * {Code: 1 byte - код ответа ([AuthResponsePacket.Code.transportValue])}
 */
class AuthResponsePacketWriter : PacketWriter() {
    override fun writeBody(out: DataOutputStream, packet: Packet) {
        packet as AuthResponsePacket
        out.write(byteArrayOf(packet.code.transportValue))
    }
}