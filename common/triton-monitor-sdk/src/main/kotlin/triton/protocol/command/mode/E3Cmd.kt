package triton.protocol.command.mode

import triton.protocol.command.AbstractCmd

/**
 * Переключение режима отображения ЭКГ на 3 кривые
 */
class E3Cmd : AbstractCmd() {
    override val header: String
        get() = "E3"
}