package triton.protocol.enums

/**
 * Флаги состояния датчиков.
 */
enum class ProbeFlags(val code: UInt) {
    SpO2ProbeOn(0x01u),
    EcgProbeOn(0x02u),
    CapNoProbeOn(0x04u),
    T1ProbeOn(0x08u),
    T2ProbeOn(0x10u),
    NibpMeasureEnd(0x20u),
    NibpMeasureError(0x40u),
    NibpCycleStarted(0x80u),
    LanPpgLowSignal(0x100u),
    LanAsistoly(0x200u),
    LanApnoe(0x400u),
    LanNibpLowPulse(0x800u);

    fun memberOfFlagsMask(flagsMask: UInt): Boolean {
        return (code and flagsMask) == code
    }

    companion object {
        fun getEnabledFlags(flagsMask: UInt): List<ProbeFlags> {
            return values().filter {
                it.memberOfFlagsMask(flagsMask)
            }
        }
    }
}