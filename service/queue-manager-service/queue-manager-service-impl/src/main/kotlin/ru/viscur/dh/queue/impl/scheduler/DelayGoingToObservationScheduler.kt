package ru.viscur.dh.queue.impl.scheduler

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.transaction.desc.config.annotation.Tx

/**
 * Created at 15.11.2019 14:51 by SherbakovaMA
 *
 * Поиск пациентов, которые долго имеют статус [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.GOING_TO_OBSERVATION]
 * и вызов для них откладку приема [QueueManagerService.delayGoingToObservation]
 */
@Component
class DelayGoingToObservationScheduler(
        private val queueManagerService: QueueManagerService,
        private val patientService: PatientService
) {
    /**
     * Каждые 10 сек
     */
    @Scheduled(cron = "1/10 * * * * *")
    @Tx
    fun delay() {
        patientService.withLongGoingToObservation().forEach {
            queueManagerService.delayGoingToObservation(it)
        }
    }
}