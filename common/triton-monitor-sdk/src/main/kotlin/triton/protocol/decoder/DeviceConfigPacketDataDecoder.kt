package triton.protocol.decoder

import triton.protocol.enums.ReturnCode
import triton.protocol.packet.DeviceConfigPacketData
import java.net.DatagramPacket

class DeviceConfigPacketDataDecoder : Decoder<DeviceConfigPacketData> {
    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(0, "LR")

    override fun decode(packet: DatagramPacket): DeviceConfigPacketData {
        val data = packet.data
        val context = DecodeContext(2)
        return DeviceConfigPacketData(
                ReturnCode.byCode(readWord(data, context)),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readLimitSet(data, context),
                readByte(data, context),
                readByte(data, context),
                readSoundSet(data, context)

        )
    }


    private fun readLimitSet(data: ByteArray, context: DecodeContext): DeviceConfigPacketData.LimitSet {
        return DeviceConfigPacketData.LimitSet(
                readWord(data, context),
                readWord(data, context),
                readByte(data, context) != 0.toUByte()
        )
    }

    private fun readSoundSet(data: ByteArray, context: DecodeContext): DeviceConfigPacketData.SoundSet {
        return DeviceConfigPacketData.SoundSet(
                readByte(data, context),
                readByte(data, context),
                readByte(data, context)
        )
    }
}