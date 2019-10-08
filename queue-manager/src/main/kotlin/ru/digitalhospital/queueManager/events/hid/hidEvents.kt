package ru.digitalhospital.queueManager.events.hid

import org.hid4java.HidDevice

data class DeviceDetached(val device: HidDevice)

data class DeviceAttached(val device: HidDevice)

data class DeviceFailure(val device: HidDevice)
