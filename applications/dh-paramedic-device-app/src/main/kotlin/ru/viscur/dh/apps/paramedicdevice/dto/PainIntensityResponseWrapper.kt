package ru.viscur.dh.apps.paramedicdevice.dto

import ru.viscur.dh.apps.paramedicdevice.enums.*

/**
 * Обертка ответа приложения для монитора шкалы боли
 *
 * @param status [PainIntensityStatus]
 * @param value Значение измерения интенсивности боли
 * @param error Ошибка
 *
 * @see "https://gitlab.com/digital-hospital/pain-intensity-monitor"
 */
data class PainIntensityResponseWrapper(
        val status: PainIntensityStatus,
        val value: Int?,
        val error: String?
)