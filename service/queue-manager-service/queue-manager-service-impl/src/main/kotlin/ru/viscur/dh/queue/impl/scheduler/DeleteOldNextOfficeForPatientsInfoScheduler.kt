package ru.viscur.dh.queue.impl.scheduler

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.transaction.desc.config.annotation.Tx

/**
 * Created at 15.11.2019 14:51 by SherbakovaMA
 *
 * Удаление устаревшей информации [QueueManagerService.deleteOldNextOfficeForPatientsInfo]
 */
@Component
@EnableScheduling
@EnableAutoConfiguration
class DeleteOldNextOfficeForPatientsInfoScheduler(
        private val queueManagerService: QueueManagerService
) {
    /**
     * Каждые 10 сек
     */
    @Scheduled(cron = "0/10 * * * * *")
    @Tx
    fun deleteOld() {
        queueManagerService.deleteOldNextOfficeForPatientsInfo()
    }
}