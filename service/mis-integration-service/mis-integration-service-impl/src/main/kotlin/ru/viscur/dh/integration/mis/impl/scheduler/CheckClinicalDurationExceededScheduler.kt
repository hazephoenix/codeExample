package ru.viscur.dh.queue.impl.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.ClinicalImpressionService
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.api.util.CLINICAL_IMPRESSION
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.fhir.model.utils.durationInSeconds
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.integration.practitioner.app.api.PractitionerAppEventPublisher

/**
 * Created at 15.11.2019 14:51 by SherbakovaMA
 *
 * Оповещение ответственных врачей о том, что кто-то из пациентов под ответственностью превысил регламентное время обслуживания обращения
 */
@Component
class CheckClinicalDurationExceededScheduler(
        private val practitionerAppEventPublisher: PractitionerAppEventPublisher,
        private val observationDurationService: ObservationDurationEstimationService,
        private val clinicalImpressionService: ClinicalImpressionService
) {
    /**
     * Каждую минуту
     */
    @Scheduled(cron = "0 * * * * *")
    fun check() {
        val severityToDefaultDuration = observationDurationService.severitiesToDefaultDuration(CLINICAL_IMPRESSION)
        val allActive = clinicalImpressionService.allActive()
        val now = now()
        allActive.map {
            val duration = durationInSeconds(it.date, now)
            val severity = it.extension.severity
            val defaultDuration = severityToDefaultDuration[severity.name]
                    ?: throw Exception("not found default duration for severity '${severity.name}'")
            val timeAfterDefaultPassed = duration - defaultDuration
            //т к шедулер раз в минуту, то в проверку должны попадать превышения от 0 до 59, чтобы после превышения оповещение сработало ровно 1 раз
            if (timeAfterDefaultPassed in 0 until SECONDS_IN_MINUTE) {
                it.assessor?.run {
                    practitionerAppEventPublisher.publishPatientServiceTimeElapsed(setOf(this.id()), it)
                }
            }
        }
    }
}