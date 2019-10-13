package ru.viscur.dh.apps.paramedicdevice.dto

/**
 * Created at 30.09.2019 11:22 by TimochkinEA
 *
 * Ответ на запрос данных с мед. оборудования
 */
data class DeviceInfoResponse(
        val responseId: String,
        val forId: String
)
