package ru.viscur.dh.paramedic.devicemonitor.mocks

import ru.viscur.dh.paramedic.devicemonitor.meddevice.IMedDevice

/**
 * Created at 30.09.2019 15:54 by TimochkinEA
 */
interface FakeMedDevice : IMedDevice {

    val delayToResult: Long
}
