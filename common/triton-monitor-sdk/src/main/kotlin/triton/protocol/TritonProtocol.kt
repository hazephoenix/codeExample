package triton.protocol

import triton.protocol.command.AbstractCmd
import triton.protocol.decoder.*
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress

class TritonProtocol {

    fun decodePacket(packet: DatagramPacket): Any {
        val decoder = DECODERS.find { it.isAcceptable(packet) }
                ?: throw UnsupportedPacket()
        return decoder.decode(packet)
    }


    class UnsupportedPacket : Exception()

    companion object {
        private val DECODERS = listOf(
                DeviceConfigPacketDataDecoder(),
                LinkRequestDecoder(),
                SimpleReturnCodePacketDecoder(),
                ValuePacketDataDecoder(),
                ValuePacketDataXfDecoder(),
                VersionPacketDataDecoder(),
                WavePacketData1Decoder(),
                WavePacketData3Decoder(),
                WavePacketDataXfDecoder()
        )
    }
}

fun DatagramSocket.send(cmd: AbstractCmd, address: InetAddress, port: Int) {
    val encoded = cmd.encode()
    this.send(DatagramPacket(encoded, encoded.size, address, port))
}