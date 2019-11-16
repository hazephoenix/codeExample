package ru.viscur.dh.integration.mis.api.dto

import java.util.*

/**
 * Created at 13.11.2019 15:38 by SherbakovaMA
 *
 * Задание периода для запросов
 */
data class PeriodRequestBody(
        val start: Date,
        val end: Date
)