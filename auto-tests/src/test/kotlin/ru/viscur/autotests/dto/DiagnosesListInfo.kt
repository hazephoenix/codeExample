package ru.viscur.autotests.dto

data class DiagnosesListInfo (
        val diagnoses: List<DiagnosisInfo>
)

data class DiagnosisInfo (
        val code: String,
        val system: String,
        val probability: Double
)

