package ru.viscur.dh.apps.paramedicdevice.dto

/**
 * Created at 11.10.2019 11:44 by TimochkinEA
 *
 * Ответы от устройств
 */

/**
 * Ответ от ростомера/весов
 *
 * @property value     значение измерения
 * @property unit      единица измерения
 * @property code      код ответа
 * @property message   сообщение, как правило полезно при ошибках, прилетает всегда
 */
data class TvesResponse(
        val value: String,
        val unit: String,
        val code: String,
        val message: String
)
