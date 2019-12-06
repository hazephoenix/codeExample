package ru.viscur.dh.integration.practitioner.app.rest.push.protocol

import org.slf4j.LoggerFactory
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io.AuthRequestPacketReader
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io.AuthResponsePacketWriter
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io.KeepAlivePacketReader
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io.PushMessagePacketWriter
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.PacketType
import java.io.DataInputStream
import java.io.DataOutputStream

class Protocol {
    private val readers = mapOf(
            PacketType.AuthRequest to AuthRequestPacketReader(),
            PacketType.KeepAlive to KeepAlivePacketReader()
    )
    private val writers = mapOf(
            PacketType.AuthResponse to AuthResponsePacketWriter(),
            PacketType.PushMessage to PushMessagePacketWriter()
    )


    fun readPacket(input: DataInputStream): Packet {
        val header = ByteArray(2);
        input.read(header)
        val type = findPacketType(header);
        val reader = readers[type]
                ?: throw IllegalStateException("Can't find reader for packet type $type")
        return reader.read(input);
    }

    fun writePacket(out: DataOutputStream, packet: Packet) {
        val writer = writers[packet.type]
                ?: throw IllegalStateException("Can't find writer for packet type ${packet.type}")
        writer.write(out, packet)
    }


    private fun findPacketType(header: ByteArray) =
            PacketType
                    .values()
                    .find { it.header.contentEquals(header) }

    companion object {
        private val logger = LoggerFactory.getLogger(Protocol::class.java);
    }

}