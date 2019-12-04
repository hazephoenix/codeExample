package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io

import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.AuthRequestPacket
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import java.io.DataInputStream

/**
 * Reader пакета [AuthRequestPacket]
 *
 * Формат пакета
 *  * {header: 2 bytes (читается в [ru.viscur.dh.integration.practitioner.app.rest.push.protocol.Protocol.readPacket])}
 *  * {LSz: 4 byte - размер логина в байтах}
 *  * {LSz: 4 byte - размер пароля в байтах }
 *  * {логин: LSz byte (UTF-8 String) }
 *  * {пароль: PSz byte (UTF-8 String) }
 */
class AuthRequestPacketReader : PacketReader {
    override fun read(input: DataInputStream): Packet {
        val loginBytesCount = input.readInt()
        val passwordBytesCount = input.readInt()
        var buf = ByteArray(loginBytesCount)
        input.read(buf)
        val login = String(buf, Charsets.UTF_8)
        buf = ByteArray(passwordBytesCount)
        input.read(buf)
        val password = String(buf, Charsets.UTF_8)
        return AuthRequestPacket(login, password)
    }
}