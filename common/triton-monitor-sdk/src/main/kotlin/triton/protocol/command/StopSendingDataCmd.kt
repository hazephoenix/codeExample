package triton.protocol.command

/**
 * Z1: Получив эту команду, монитор прекращает отправлять данные
 */
class StopSendingDataCmd : AbstractCmd() {
    override val header: String
        get() = "Z1"
}