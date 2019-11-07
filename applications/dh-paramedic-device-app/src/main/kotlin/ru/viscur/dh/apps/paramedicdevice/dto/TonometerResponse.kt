package ru.viscur.dh.apps.paramedicdevice.dto

import java.sql.*

/**
 * Ответ от тонометра
 *
 * @param dateTime Дата и время измерения
 * @param systolicBP Систоличесое (верхнее) АД
 * @param diastolicBP Диастолическое (нижнее) АД
 * @param meanArterialBP Среднее АД
 * @param pulseRate Частота пульса
 * @param pressurizationSetupValue Значение герметизации (надува) рукава в мм.рт.ст.
 * @param maxPulseAmplitude Максимальная амплитуда пульса
 * @param tonometerModel Модель тонометра
 * @param error Ошибка измерения
 */
data class TonometerResponse(
        val dateTime: Timestamp,
        val systolicBP: Int,
        val diastolicBP: Int,
        val meanArterialBP: Int,
        val pulseRate: Int,
        val pressurizationSetupValue: Int,
        val maxPulseAmplitude: Int,
        val tonometerModel: String,
        val error: TonometerError
)
