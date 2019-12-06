package triton.protocol.command

class ToggleSoundCmd(private val turnOn: Boolean) : AbstractCmd() {
    override val header: String
        get() = "SO"

    override fun encode(buffer: MutableList<Byte>) {
        // 2 байта.
        //   * !0 - включить звук
        //   * 0 - выключить звук
        buffer.add(1)
        buffer.add(0)
    }
}