package ru.viscur.dh.apps.paramedicdevice.mocks

import ru.viscur.dh.apps.paramedicdevice.meddevice.IMedDevice

/**
 * Created at 30.09.2019 15:54 by TimochkinEA
 */
interface FakeMedDevice : IMedDevice {

    val delayToResult: Long
}
