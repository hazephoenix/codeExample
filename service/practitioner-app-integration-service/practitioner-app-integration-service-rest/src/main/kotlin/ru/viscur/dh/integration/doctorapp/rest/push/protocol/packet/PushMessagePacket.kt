package ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet

class PushMessagePacket(
        val data: Any
) : Packet(PacketType.PushMessage)