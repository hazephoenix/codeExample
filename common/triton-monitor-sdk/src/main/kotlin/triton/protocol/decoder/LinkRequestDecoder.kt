package triton.protocol.decoder

import triton.protocol.packet.LinkRequest
import java.lang.Exception
import java.net.DatagramPacket

class LinkRequestDecoder : Decoder<LinkRequest> {
    override fun isAcceptable(packet: DatagramPacket): Boolean =
            packet.hasSizeAndHeader(10, "LINK")

    override fun decode(packet: DatagramPacket): LinkRequest {
        if (!isAcceptable(packet)) {
            throw Exception("Can't decode LinkRequest from packet: Wrong UPD packet")
        }
        return LinkRequest(
                packet.data.copyOfRange(4, 10)
                        .map { it.toUByte() }
                        .toUByteArray()
        )
    }

}
