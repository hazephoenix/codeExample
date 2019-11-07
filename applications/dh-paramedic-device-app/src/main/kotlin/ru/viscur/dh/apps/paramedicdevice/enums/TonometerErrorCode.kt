package ru.viscur.dh.apps.paramedicdevice.enums

/**
 * Коды ошибок тонометра
 */
enum class TonometerErrorCode(val display: String) {
    E00("Normal measurement has finished"),
    E11("Pressure is not applied at the start of the measurement."),
    E15("Pressure is not applied at the start of the measurement."),
    E12("Pressure cannot be applied within a certain period of time."),
    E13("Inflation speed is too fast."),
    E21("The exhaust speed is too slow."),
    E22("The exhaust speed is too fast."),
    E23("Excess pressure was detected."),
    E24("The time limit for one measurement was exceeded."),
    E42("The pressure is insufficient."),
    E43("Pulse cannot be detected."),
    E44("The patient may have moved during measurement."),
    E45("Diastolic blood pressure cannot be determined."),
    E46("Mean arterial blood pressure cannot be determined."),
    E48("Systolic blood pressure cannot be determined."),
    E61("Pulse cannot be determined."),
    E63("The blood pressure value is ‘out of range’.")
}
