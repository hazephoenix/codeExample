package triton.protocol.packet

import triton.protocol.enums.ReturnCode

class StopSendingDataReturnCode(override val returnCode: ReturnCode) : ReturnCodePacket() {
}