package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io

import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import java.io.DataInputStream

interface PacketReader {

    fun read(input: DataInputStream): Packet
}