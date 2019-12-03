package ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet

import ru.viscur.dh.integration.doctorapp.rest.push.protocol.io.PacketReader
import java.io.DataInputStream

class KeepAlivePacketReader : PacketReader {
    override fun read(input: DataInputStream): Packet {
        return KeepAlivePacket()
    }
}