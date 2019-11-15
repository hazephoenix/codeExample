package ru.viscur.dh.integration.mis.api.dto

/**
 * Created at 12.11.2019 16:05 by SherbakovaMA
 *
 * Информация о регламентном значении продолжительности обслуживания пациентов с опр. степенью тяжести
 *
 * @param severity степень тяжести
 * @param severityDisplay отображаемая степень тяжести
 * @param defaultDuration регламентное время обслуживания (в секундах)
 * @param autoRecalc автоматическое изменение
 */
data class ClinicalImpressionDeafualtDurationDto(
        val severity: String,
        val severityDisplay: String,
        val defaultDuration: Int,
        val autoRecalc: Boolean
)