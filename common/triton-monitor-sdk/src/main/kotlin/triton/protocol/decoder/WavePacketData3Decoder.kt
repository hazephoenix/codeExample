package triton.protocol.decoder

import triton.protocol.packet.WavePacketData3
import java.net.DatagramPacket

class WavePacketData3Decoder : Decoder<WavePacketData3> {
    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(516, "<3")

    override fun decode(packet: DatagramPacket): WavePacketData3 {
        val context = DecodeContext(2)
        val data = packet.data;
        return WavePacketData3(
                // nPacket
                readWord(data, context),
                // wPPG
                readByteArray(data, 64, context),
                // wECG
                readByteArray(data, 128, context),
                // wRSPECG
                readByteArray(data, 32, context),
                // wCO2
                readByteArray(data, 32, context),
                // wECG2
                readByteArray(data, 128, context),
                // wECG3
                readByteArray(data, 128, context)
        )
    }
}