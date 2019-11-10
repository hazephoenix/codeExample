package triton.protocol.command

/**
 * Получение версии монитора
 */
class ReadVersionInfoCmd : AbstractCmd() {
    override val header: String
        get() = "VI"
}