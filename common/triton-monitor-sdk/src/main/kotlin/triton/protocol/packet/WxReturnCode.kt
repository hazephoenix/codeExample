package triton.protocol.packet

import triton.protocol.enums.ReturnCode

class WxReturnCode(override val returnCode: ReturnCode) : ReturnCodePacket() {
}