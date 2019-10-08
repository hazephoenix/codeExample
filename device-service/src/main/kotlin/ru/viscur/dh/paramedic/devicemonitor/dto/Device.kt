package ru.viscur.dh.paramedic.devicemonitor.dto

/**
 * Created at 01.10.2019 15:58 by TimochkinEA
 */
data class Device(
        override val resourceType: String = "Device",
        val status: DeviceStatus,
        override val identifier: Identifier
): Resource
