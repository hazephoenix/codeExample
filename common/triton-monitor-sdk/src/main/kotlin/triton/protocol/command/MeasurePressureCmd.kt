package triton.protocol.command

/**
 * Запуск измерения давления
 */
class MeasurePressureCmd : AbstractCmd() {
    override val header: String
        get() = "MP"
}