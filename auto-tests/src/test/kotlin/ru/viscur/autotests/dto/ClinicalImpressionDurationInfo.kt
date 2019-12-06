package ru.viscur.autotests.dto

data class ClinicalImpressionDurationInfo (
        val patientId: String,
        val start: String,
        val duration: Int,
        val defaultDuration: Int
)