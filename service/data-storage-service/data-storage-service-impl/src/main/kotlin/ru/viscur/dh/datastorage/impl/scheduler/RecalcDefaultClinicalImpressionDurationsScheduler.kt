package ru.viscur.dh.datastorage.impl.scheduler

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.fhir.model.utils.toStringFmtWithSeconds

/**
 * Created at 12.11.2019 14:51 by SherbakovaMA
 *
 * Шедулер для пересчета регламентного времени обслуживания пациентов по степени тяжести
 * в зависимости от включенных соответсвующих настроек
 */
@Component
@EnableScheduling
class RecalcDefaultClinicalImpressionDurationsScheduler(
        private val observationDurationService: ObservationDurationEstimationService
) {

    companion object {
        private val log = LoggerFactory.getLogger(RecalcDefaultClinicalImpressionDurationsScheduler::class.java)
    }

    /**
     * Каждый день в 08:00
     */
    @Scheduled(cron = "0 0 8 * * *")
    fun recalc() {
        observationDurationService.recalcDefaultClinicalImpressionDurations()
        log.info("RecalcDefaultClinicalImpressionDurationsScheduler.recalc() executed at ${now().toStringFmtWithSeconds()}")
    }
}