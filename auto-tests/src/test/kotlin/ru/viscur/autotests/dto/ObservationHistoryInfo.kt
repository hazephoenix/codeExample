package ru.viscur.autotests.dto

data class ObservationHistoryInfo(
        val patientId: String,
        val fireDate: String,
        val code: String,
        val duration: Int
)