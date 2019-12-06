package triton.protocol.command.mode

import triton.protocol.command.AbstractCmd

/**
 * Переключение режима отображения ЭКГ на 1 кривую
 */
class E1Cmd : AbstractCmd() {
    override val header: String
        get() = "E1"
}