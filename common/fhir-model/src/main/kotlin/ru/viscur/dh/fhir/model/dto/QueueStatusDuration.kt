package ru.viscur.dh.fhir.model.dto

import java.util.*

/**
 * Created at 12.11.2019 9:22 by SherbakovaMA
 *
 * Информация о продолжительности проведения пациента в этапах очереди
 *
 * @param patientId id пациента
 * @param fireDate дата начала
 * @param status статус в очереди
 * @param officeId id кабинета, если статус относится к кабинету
 * @param duration продолжительность (в секундах)
 */
data class QueueStatusDuration(
        val patientId: String,
        val fireDate: Date,
        val status: String,
        val officeId: String?,
        val duration: Int
)