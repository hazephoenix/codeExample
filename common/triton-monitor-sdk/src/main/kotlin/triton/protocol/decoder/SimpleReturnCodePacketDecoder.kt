package triton.protocol.decoder

import triton.protocol.enums.ReturnCode
import triton.protocol.packet.*
import java.net.DatagramPacket

class SimpleReturnCodePacketDecoder : Decoder<ReturnCodePacket> {

    private val supportedHeaders = setOf("E1", "E3", "W1", "WX", "Z0", "Z1", "SO", "MP")

    override fun isAcceptable(packet: DatagramPacket): Boolean {
        if (packet.length != 4) {
            return false
        }
        return supportedHeaders.contains(readHeader(packet))
    }


    override fun decode(packet: DatagramPacket): ReturnCodePacket {
        if (!isAcceptable(packet)) {
            throw Exception("Can't read ReturnCodePacket: Bad UDP packet")
        }
        val header = readHeader(packet)
        val data = packet.data
        return when (header) {
            "E1" -> E1ReturnCode(readCode(data))
            "E3" -> E3ReturnCode(readCode(data))
            "W1" -> W1ReturnCode(readCode(data))
            "WX" -> WxReturnCode(readCode(data))
            "Z0" -> StartSendingDataReturnCode(readCode(data))
            "Z1" -> StopSendingDataReturnCode(readCode(data))
            "SO" -> ToggleSoundReturnCode(readCode(data))
            "MP" -> MeasurePressureReturnCode(readCode(data))
            else -> throw IllegalStateException("Unsupported header $header")
        }
    }

    private fun readCode(data: ByteArray) = ReturnCode.byCode(readWord(data, 2))


    private fun readHeader(packet: DatagramPacket): String {
        return String(packet.data.copyOfRange(0, 2), Charsets.US_ASCII)
    }

}