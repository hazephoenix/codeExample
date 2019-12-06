package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet

class PushMessagePacket(
        val data: Any
) : Packet(PacketType.PushMessage)