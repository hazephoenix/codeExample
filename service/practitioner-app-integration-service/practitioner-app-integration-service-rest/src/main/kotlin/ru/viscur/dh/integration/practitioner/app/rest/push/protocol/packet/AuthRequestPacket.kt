package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet

class AuthRequestPacket(
        val login: String,
        val password: String
) : Packet(PacketType.AuthRequest)