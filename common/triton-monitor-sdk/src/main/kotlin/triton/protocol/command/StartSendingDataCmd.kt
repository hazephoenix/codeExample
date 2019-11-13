package triton.protocol.command

/**
 * Получив эту команду, монитор начинает отправлять данные
 */
class StartSendingDataCmd : AbstractCmd() {
    override val header: String
        get() = "Z0"
}