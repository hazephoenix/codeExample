package ru.viscur.dh.fhir.model.dto

import java.util.*

/**
 * Created at 12.11.2019 9:22 by SherbakovaMA
 *
 * Информация о продолжительности проведения услуги пациенту
 *
 * @param patientId id пациента
 * @param fireDate дата начала
 * @param code код услуги
 * @param duration продолжительность (в секундах)
 */
data class ObservationDuration(
        val patientId: String,
        val fireDate: Date,
        val code: String,
        val duration: Int
)