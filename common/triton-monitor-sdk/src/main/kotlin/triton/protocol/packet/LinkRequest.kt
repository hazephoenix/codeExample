package triton.protocol.packet

import java.net.DatagramPacket

class LinkRequest(
        val mac: UByteArray
) {
    val macAsString: String
        get() {
            if (mac.size != 6) {
                return "--:--:--:--:--:--:"
            }
            return String.format("%02x:%02x:%02x:%02x:%02x:%02x", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5])
        }

}