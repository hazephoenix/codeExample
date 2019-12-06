package ru.viscur.autotests.dto

data class PatientQueueHistoryInfo(
        val patientId: String,
        val fireDate: String,
        val status: String,
        val officeId: String?,
        val duration: Int
)