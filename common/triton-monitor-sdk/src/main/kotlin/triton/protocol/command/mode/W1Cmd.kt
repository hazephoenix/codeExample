package triton.protocol.command.mode

import triton.protocol.command.AbstractCmd

/**
 * Переключение режима отображения волн на исходный набор кривых
 */
class W1Cmd : AbstractCmd() {
    override val header: String
        get() = "W1"
}