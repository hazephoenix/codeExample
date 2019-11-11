package triton.protocol.packet

import triton.protocol.enums.ReturnCode

class W1ReturnCode(override val returnCode: ReturnCode) : ReturnCodePacket() {
}