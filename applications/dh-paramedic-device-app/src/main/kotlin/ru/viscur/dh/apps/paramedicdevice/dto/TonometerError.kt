package ru.viscur.dh.apps.paramedicdevice.dto

import ru.viscur.dh.apps.paramedicdevice.enums.*

/**
 * Ошибка измерения тонометра
 */
data class TonometerError(
        val code: TonometerErrorCode,
        val display: String
)