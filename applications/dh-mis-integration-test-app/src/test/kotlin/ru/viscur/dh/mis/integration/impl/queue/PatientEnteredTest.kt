package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.util.OFFICE_130
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus.GOING_TO_OBSERVATION
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus.ON_OBSERVATION
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus.IN_QUEUE
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 09.11.2019 10:50 by SherbakovaMA
 *
 * Тест на метод "пациент вошел в кабинет" [QueueManagerService.patientEntered]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class PatientEnteredTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    @Test
    fun `going, not first`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val checkP = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.patientEntered(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = ON_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `going, first`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val checkP = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.patientEntered(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = checkP, status = ON_OBSERVATION),
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `on observation`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val checkP = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = ON_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = ON_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.patientEntered(checkP, officeId)
        //ничего не поменялось. он уже на приеме
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = ON_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `in queue`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val checkP = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.patientEntered(checkP, officeId)
        //ничего не поменялось. его очередь не настала
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `not in queue`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val checkP = forTestService.createPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.patientEntered(checkP, officeId)
        //ничего не поменялось. он не в очереди
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }
}