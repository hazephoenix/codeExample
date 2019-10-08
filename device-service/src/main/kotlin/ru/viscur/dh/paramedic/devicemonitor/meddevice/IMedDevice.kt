package ru.viscur.dh.paramedic.devicemonitor.meddevice

import ru.viscur.dh.paramedic.devicemonitor.dto.Device
import ru.viscur.dh.paramedic.devicemonitor.dto.DeviceStatus
import ru.viscur.dh.paramedic.devicemonitor.dto.Observation

/**
 * Created at 30.09.2019 15:51 by TimochkinEA
 *
 * Описание медицинского устройства
 */
interface IMedDevice {

    /**
     * Получить измерения с устройства
     * @return результат измерения
     */
    fun take(): Observation

    /**
     * Информация об устройстве
     */
    fun info(): Device = Device(status = DeviceStatus.Unknown)
}
