package ru.viscur.dh.integration.practitioner.app.rest.push.protocol.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.Packet
import ru.viscur.dh.integration.practitioner.app.rest.push.protocol.packet.PushMessagePacket
import java.io.DataOutputStream

/**
 * Writer пакета [PushMessagePacket]
 *
 * Формат сообщения:
 *  * {Header: 2 byte}
 *  * {DSz: 4 byte - размер сообщения}
 *  * {Data: DSz byte - текстовые данные в UTF-8 (JSON)}
 */
class PushMessagePacketWriter : PacketWriter() {
    private val objectMapper = ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    override fun writeBody(out: DataOutputStream, packet: Packet) {
        packet as PushMessagePacket
        val data = objectMapper.writeValueAsBytes(
                mapOf(
                        "messageType" to packet.data.javaClass.simpleName,
                        "content" to packet.data
                )
        )
        out.writeInt(data.size)
        out.write(data)
    }
}