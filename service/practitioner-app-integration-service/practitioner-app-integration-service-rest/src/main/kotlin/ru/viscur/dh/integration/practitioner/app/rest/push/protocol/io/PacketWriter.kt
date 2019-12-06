package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io

import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import java.io.DataOutputStream

abstract class PacketWriter {
    fun write(out: DataOutputStream, packet: Packet) {
        writeHeader(out, packet)
        writeBody(out, packet)
    }

    private fun writeHeader(out: DataOutputStream, packet: Packet) {
        out.write(packet.type.header)
    }

    protected abstract fun writeBody(out: DataOutputStream, packet: Packet)


}