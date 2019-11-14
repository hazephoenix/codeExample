package ru.viscur.dh.integration.mis.api.dto

import java.util.*

/**
 * Created at 12.11.2019 16:02 by SherbakovaMA
 *
 * Информация о продолжительности обращения пациента
 * @param patientId id пациента
 * @param start время начала
 * @param duration продолжительность (в секундах)
 * @param defaultDuration регламентное значение пролодложительности
 */
data class ClinicalImpressionDurationDto(
        val patientId: String,
        val start: Date,
        val duration: Int,
        val defaultDuration: Int
)