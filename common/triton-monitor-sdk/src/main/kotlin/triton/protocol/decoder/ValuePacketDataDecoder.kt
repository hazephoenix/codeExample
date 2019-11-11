package triton.protocol.decoder

import triton.protocol.packet.ValuePacketData
import java.lang.Exception
import java.net.DatagramPacket

class ValuePacketDataDecoder : Decoder<ValuePacketData> {

    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(36, "((")

    override fun decode(packet: DatagramPacket): ValuePacketData {
        if (!isAcceptable(packet)) {
            throw Exception("Can't decode ValuePacketData from packet: Wrong UPD packet")
        }
        // 2 байта - заголовок, игнорируем, проверили в isAcceptable
        // 2 байта - выравнивание, игнорируем
        val context = DecodeContext(4)
        val data = packet.data
        return ValuePacketData(
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
                readByte(data, context)
        );
    }
}