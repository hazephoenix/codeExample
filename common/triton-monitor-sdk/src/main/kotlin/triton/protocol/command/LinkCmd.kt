package triton.protocol.command

import triton.protocol.packet.LinkRequest
import java.net.DatagramPacket
import java.net.Inet4Address

/**
 * Назначение монитору IP адреса (перевод в рабочий режим)
 */
class LinkCmd(
        /**
         * Запрос на получение IP адреса (получен от монитора)
         */
        private val sourceRequest: LinkRequest,

        /**
         * IP адрес который назначаем устройству
         */
        private val ipAddress: Inet4Address
) : AbstractCmd() {
    override val header: String
        get() = "LINK"


    override fun encode(buffer: MutableList<Byte>) {
        buffer.addAll(sourceRequest.mac.map { it.toByte() })
        buffer.addAll(ipAddress.address)
    }
}