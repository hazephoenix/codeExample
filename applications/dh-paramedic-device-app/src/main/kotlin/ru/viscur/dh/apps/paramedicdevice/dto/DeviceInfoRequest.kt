package ru.viscur.dh.apps.paramedicdevice.dto

import java.util.*

/**
 * Created at 30.09.2019 11:12 by TimochkinEA
 *
 * Запрос на получение данных с мед. оборудования
 */
data class DeviceInfoRequest(
        val requestId: String,
        val encounterId: String,
        val requestDate: Date
)
