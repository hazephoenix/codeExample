package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.ServiceRequestService
import ru.viscur.dh.datastorage.api.util.OFFICE_130
import ru.viscur.dh.datastorage.api.util.OFFICE_202
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus.*
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 09.11.2019 10:50 by SherbakovaMA
 *
 * Тест на метод "Вызов пациента на обследование в кабинет" [QueueManagerService.forceSendPatientToObservation]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class ForceSendPatientToObservationTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var serviceRequestService: ServiceRequestService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var resourceService: ResourceService


    @Test
    fun `in queue`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val inQue2 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val checkP = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE),
                QueueItemSimple(patientId = checkP, status = IN_QUEUE)
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE),
                QueueItemSimple(patientId = inQue2, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `in another queue`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val checkP = forTestService.createPatientWithQueueItem(officeId = OFFICE_202, queueStatus = IN_QUEUE, index = 0, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = ON_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = going1, status = ON_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = checkP, status = IN_QUEUE)
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = going1, status = ON_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `not in queue, going exists`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val going1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val going2 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = GOING_TO_OBSERVATION, index = i++)
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val checkP = forTestService.createPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = going2, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = going1, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = going2, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `not in queue, in_queue exists`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val inQue1 = forTestService.createPatientWithQueueItem(officeId = officeId, queueStatus = IN_QUEUE, index = i++)
        val checkP = forTestService.createPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = inQue1, status = IN_QUEUE)
        ))))
    }

    @Test
    fun `not in queue, no queue in office, office is ready`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val checkP = forTestService.createPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        forTestService.updateOfficeStatuses()

        queueManagerService.officeIsReady(officeId)
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.READY, items = listOf(
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION)
        ))))
    }

    @Test
    fun `not in queue, no queue in office, office is busy`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val checkP = forTestService.createPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION)
        ))))
    }

    @Test
    fun `not in queue, no queue in office, no service request in office`() {
        forTestService.cleanDb()
        var i = 0
        val officeId = OFFICE_130
        val checkP = forTestService.createPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        forTestService.updateOfficeStatuses()

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))

        queueManagerService.forceSendPatientToObservation(checkP, officeId)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkP, status = GOING_TO_OBSERVATION)
        ))))
    }
}