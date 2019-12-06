package triton.protocol.decoder

import triton.protocol.enums.ReturnCode
import triton.protocol.packet.VersionPacketData
import java.net.DatagramPacket

class VersionPacketDataDecoder : Decoder<VersionPacketData> {
    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(10, "VI")

    override fun decode(packet: DatagramPacket): VersionPacketData {
        val data = packet.data
        val context = DecodeContext(2)
        return VersionPacketData(
                ReturnCode.byCode(readWord(data, context)),
                readWord(data, context),
                VersionPacketData.Version(
                        readWord(data, context),
                        readWord(data, context)
                )
        )
    }
}