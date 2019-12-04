package ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet

class AuthResponsePacket(val code: Code) : Packet(PacketType.AuthResponse) {
    enum class Code(val transportValue: Byte) {
        Ok(0x00),
        IncorrectUserOrPassword(0x01)
    }
}