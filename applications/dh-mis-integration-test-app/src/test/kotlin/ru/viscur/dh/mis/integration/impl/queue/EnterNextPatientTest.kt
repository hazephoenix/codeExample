package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.QueueItemSimple
import ru.viscur.dh.apps.misintegrationtest.util.QueueOfOfficeSimple
import ru.viscur.dh.datastorage.api.util.OFFICE_130
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus.*
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 09.11.2019 10:50 by SherbakovaMA
 *
 * Тест на метод "Пригласить след. пациента в очередь в кабинет" [QueueManagerService.enterNextPatient]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class EnterNextPatientTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    @Test
    fun `no queue, busy`() {
        forTestService.cleanDb()
        val officeId = OFFICE_130
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))

        queueManagerService.enterNextPatient(officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))
    }

    @Test
    fun `no queue, ready`() {
        forTestService.cleanDb()
        val officeId = OFFICE_130
        forTestService.updateOfficeStatuses()

        queueManagerService.officeIsReady(officeId)
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.READY, items = listOf(
        ))))

        queueManagerService.enterNextPatient(officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.READY, items = listOf(
        ))))
    }

    @Test
    fun `has in_queue only`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val inQue2 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))

        queueManagerService.enterNextPatient(officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = inQue1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `has going`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val inQue2 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))

        queueManagerService.enterNextPatient(officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `has obs, going`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val onObs1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = ON_OBSERVATION, index = i++)
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val inQue2 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = onObs1, status = ON_OBSERVATION),
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))

        queueManagerService.enterNextPatient(officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = onObs1, status = ON_OBSERVATION),
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))
    }
}