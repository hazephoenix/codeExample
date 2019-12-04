package ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet

class AuthRequestPacket(
        val login: String,
        val password: String
) : Packet(PacketType.AuthRequest)