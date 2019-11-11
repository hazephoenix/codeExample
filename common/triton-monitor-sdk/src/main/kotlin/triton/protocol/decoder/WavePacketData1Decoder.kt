package triton.protocol.decoder

import triton.protocol.packet.WavePacketData1
import java.net.DatagramPacket

class WavePacketData1Decoder : Decoder<WavePacketData1> {

    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(260, "<<")

    override fun decode(packet: DatagramPacket): WavePacketData1 {
        val context = DecodeContext(2)
        val data = packet.data;
        return WavePacketData1(
                readWord(data, context),
                readByteArray(data, 64, context),
                readByteArray(data, 128, context),
                readByteArray(data, 32, context),
                readByteArray(data, 32, context)
        )
    }

}