package triton.protocol.packet

import triton.protocol.enums.ReturnCode

abstract class ReturnCodePacket {
    abstract val returnCode: ReturnCode
}