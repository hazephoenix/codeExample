package triton.protocol.command.mode

import triton.protocol.command.AbstractCmd

/**
 * Переключение режима отображения волн на расширенный набор кривых
 */
class WxCmd : AbstractCmd() {
    override val header: String
        get() = "WX"
}