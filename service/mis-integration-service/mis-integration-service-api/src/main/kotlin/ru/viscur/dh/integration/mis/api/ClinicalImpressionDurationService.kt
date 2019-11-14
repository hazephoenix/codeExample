package ru.viscur.dh.integration.mis.api

import ru.viscur.dh.integration.mis.api.dto.ClinicalImpressionDeafualtDurationDto
import ru.viscur.dh.integration.mis.api.dto.ClinicalImpressionDurationDto
import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Created at 12.11.2019 15:47 by SherbakovaMA
 *
 * Сервис для просмотра/редактирования регламентного времени обслуживания обращения пациентов
 * и его настройки
 */
interface ClinicalImpressionDurationService {

    /**
     * Задать значение регламентного времени обслуживания обращения пациентов для указанной степени тяжести
     */
    fun updateDefaultDuration(severity: Severity, duration: Int)

    /**
     * Задать значение настройки Автокорректировка регламентного времени обслуживания по степени тяжести
     */
    fun updateConfigAutoRecalc(severity: Severity, value: Boolean)

    /**
     * Информация о продолжительности текущих/активных обращений пациентов
     */
    fun currentDurations(): List<ClinicalImpressionDurationDto>

    /**
     * Информация о регламентных значений продолжительности обслуживания обращения пациентов
     */
    fun defaultDurations(): List<ClinicalImpressionDeafualtDurationDto>
}