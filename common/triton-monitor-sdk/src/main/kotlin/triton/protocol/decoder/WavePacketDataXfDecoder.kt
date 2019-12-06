package triton.protocol.decoder

import triton.protocol.packet.WavePacketDataXf
import java.net.DatagramPacket

class WavePacketDataXfDecoder : Decoder<WavePacketDataXf> {
    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(516, "<X")

    override fun decode(packet: DatagramPacket): WavePacketDataXf {
        val data = packet.data
        val context = DecodeContext(2)
        return WavePacketDataXf(
                // nPacket
                readWord(data, context),
                // wPPG
                readByteArray(data, 64, context),
                // wECG
                readByteArray(data, 128, context),
                // wRSPECG
                readByteArray(data, 32, context),
                // wCO2_sm
                readByteArray(data, 32, context),
                // wCO2
                readWordArray(data, 32, context),
                // wO2
                readWordArray(data, 32, context),
                // wFLOW
                readShortArray(data, 32, context),
                // wPRESSURE
                readShortArray(data, 32, context)
        )
    }
}