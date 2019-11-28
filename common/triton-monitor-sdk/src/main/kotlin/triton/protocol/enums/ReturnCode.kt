package triton.protocol.enums

import triton.TritonMonitorDevice

/**
 * Коды ответа от монитора
 */
@ExperimentalUnsignedTypes
enum class ReturnCode constructor(val code: UShort) {
    Ok(0x00_00u),
    UnknownCommand(0x00_05u),
    IncorrectSizeOfData(0x00_0Fu),
    WrongData(0x00_5Fu),
    UnknownError(0x00_FFu);

    companion object {
        fun byCode(code: UShort): ReturnCode {
            return values().find { it.code == code }
                    ?: throw Exception("Unknown error code")
        }

        fun throwOnErrorCode(opName: () -> String,  block: () -> ReturnCode) {
            val code = block()
            if (code != Ok) {
                throw Exception("Operation '$opName' complete with code: $code")
            }
        }
    }
}