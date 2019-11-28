package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.integration.mis.api.ClinicalImpressionDurationService

/**
 * Created at 12.11.2019 15:44 by SherbakovaMA
 *
 * Контроллер для просмотра/редактирования регламентного времени обслуживания обращения пациентов
 * и его настройки
 */
@RestController
@RequestMapping("/clinicalImpressionDuration")
class ClinicalImpressionDurationController(
        private val clinicalImpressionDurationService: ClinicalImpressionDurationService
) {

    /**
     * see [ClinicalImpressionDurationService.updateDefaultDuration]
     */
    @PostMapping("/duration")
    fun updateDefaultDuration(
            @RequestParam severity: String,
            @RequestParam duration: Int
    ) {
        clinicalImpressionDurationService.updateDefaultDuration(enumValueOf(severity), duration)
    }

    /**
     * see [ClinicalImpressionDurationService.updateConfigAutoRecalc]
     */
    @PostMapping("/autoRecalc")
    fun updateConfigAutoRecalc(
            @RequestParam severity: String,
            @RequestParam value: Boolean
    ) {
        clinicalImpressionDurationService.updateConfigAutoRecalc(enumValueOf(severity), value)
    }

    /**
     * see [ClinicalImpressionDurationService.currentDurations]
     */
    @GetMapping
    fun currentDurations() = clinicalImpressionDurationService.currentDurations()

    /**
     * see [ClinicalImpressionDurationService.defaultDurations]
     */
    @GetMapping("/default")
    fun defaultDurations() = clinicalImpressionDurationService.defaultDurations()
}