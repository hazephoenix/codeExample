package triton.protocol.decoder

import triton.protocol.packet.ValuePacketDataXf
import java.net.DatagramPacket

class ValuePacketDataXfDecoder : Decoder<ValuePacketDataXf> {

    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(42, "(X")

    override fun decode(packet: DatagramPacket): ValuePacketDataXf {
        if (!isAcceptable(packet)) {
            throw Exception("Can't decode ValuePacketDataXf from packet: Wrong UDP packet")
        }
        // 2 байта - заголовок, игнорируем, проверили в isAcceptable
        // 2 байта - выравнивание, игнорируем
        val context = DecodeContext(4)
        val data = packet.data
        return ValuePacketDataXf(
                // 4 байта ProbeFlags
                readDoubleWord(data, context),
                // 2 байта SpO2
                readWord(data, context),
                // 2 байта HR
                readWord(data, context),
                // 2 байта EtCO2
                readWord(data, context),
                // 2 байта fiCO2
                readWord(data, context),
                // 2 байта RSPC
                readWord(data, context),
                // 2 байта RSPECG
                readWord(data, context),
                // 2 байта T1
                readWord(data, context),
                // 2 байта T2
                readWord(data, context),
                // 2 байта NIBP_Sys
                readWord(data, context),
                // 2 байта NIBP_Dsys
                readWord(data, context),
                // 2 байта NIBP_Med
                readWord(data, context),
                // 2 байта NIBP_TCycle
                readWord(data, context),
                // 2 байта NIBP_TNextMeasure
                readWord(data, context),
                // 1 байт PPG_Filling
                readByte(data, context),
                // 1 байт ECG_Scale
                readByte(data, context),
                // 2 байта VCO2
                readWord(data, context),
                // 2 байта VO2
                readWord(data, context),
                // 2 байта VE
                readWord(data, context)
        )
    }
}