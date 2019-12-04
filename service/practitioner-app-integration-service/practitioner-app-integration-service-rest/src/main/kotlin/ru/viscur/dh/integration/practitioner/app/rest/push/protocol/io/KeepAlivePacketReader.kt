package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io

import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.KeepAlivePacket
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import java.io.DataInputStream

class KeepAlivePacketReader : PacketReader {
    override fun read(input: DataInputStream): Packet {
        return KeepAlivePacket()
    }
}