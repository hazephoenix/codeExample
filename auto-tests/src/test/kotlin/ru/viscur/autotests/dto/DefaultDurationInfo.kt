package ru.viscur.autotests.dto

data class DefaultDurationInfo(
        val severity: String,
        val severityDisplay: String,
        val defaultDuration: Int,
        val autoRecalc: Boolean
)