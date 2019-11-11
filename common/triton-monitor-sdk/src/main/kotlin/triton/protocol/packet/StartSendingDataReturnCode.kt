package triton.protocol.packet

import triton.protocol.enums.ReturnCode

class StartSendingDataReturnCode(override val returnCode: ReturnCode) : ReturnCodePacket() {
}