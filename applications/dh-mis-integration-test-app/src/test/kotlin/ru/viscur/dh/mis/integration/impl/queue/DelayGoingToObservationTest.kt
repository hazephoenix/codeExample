package ru.viscur.dh.mis.integration.impl.queue

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.ObservationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.util.OFFICE_101
import ru.viscur.dh.datastorage.api.util.OFFICE_202
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.fhir.model.utils.plusSeconds
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 15.11.2019 11:38 by SherbakovaMA
 *
 * Проверка функции Отложить прием ожидаемого пациента в кабинет [QueueManagerService.delayGoingToObservation]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class DelayGoingToObservationTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var resourceService: ResourceService

    @Test
    fun `single with status IN_QUEUE, long waiting first in_queue`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()
        val pSr1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = pSr1.first().subject!!.id()

        val officeId = OFFICE_101
        queueManagerService.officeIsReady(officeId)

        //пациент за проверяемым долго ждет своей очереди. поэтому как только вызываем delay для проверяемого - он успешно откладывается во вторую позицию
        resourceService.update(ResourceType.Patient, pId1) {
            extension.queueStatusUpdatedAt = now().plusSeconds(-40)
        }

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE)
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(checkingPId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }

    @Test
    fun `2 with status IN_QUEUE, long waiting first in_queue`() {
        //то же самое что предыдущее, но с 2мя пациентами со статусом IN_QUEUE - проверка что ставится именно вторым
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()
        val pSr1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = pSr1.first().subject!!.id()
        val pSr2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId2 = pSr2.first().subject!!.id()

        val officeId = OFFICE_101
        queueManagerService.officeIsReady(officeId)

        //пациент за проверяемым долго ждет своей очереди. поэтому как только вызываем delay для проверяемого - он успешно откладывается во вторую позицию
        resourceService.update(ResourceType.Patient, pId1) {
            extension.queueStatusUpdatedAt = now().plusSeconds(-40)
        }

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE),
                QueueItemSimple(patientId = pId2, status = PatientQueueStatus.IN_QUEUE)
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(checkingPId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.IN_QUEUE),
                QueueItemSimple(patientId = pId2, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }

    @Test
    fun `single with status IN_QUEUE, not long waiting first in_queue`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()
        val pSr1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = pSr1.first().subject!!.id()

        val officeId = OFFICE_101
        queueManagerService.officeIsReady(officeId)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE)
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(checkingPId)
        //пациент за проверяемым недолго ждет - пришел совсем недавно. не откладывается проверяемый (нужно еще подождать проверяемого до откладки)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }

    @Test
    fun `single with status IN_QUEUE, not long waiting first in_queue, with onlyIfFirstInQueueIsLongWaiting = false`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()
        val pSr1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = pSr1.first().subject!!.id()

        val officeId = OFFICE_101
        queueManagerService.officeIsReady(officeId)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE)
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(patientId = checkingPId, onlyIfFirstInQueueIsLongWaiting = false)
        //пациент за проверяемым недолго ждет - пришел совсем недавно, но фукцнию вызвали с onlyIfFirstInQueueIsLongWaiting = false, значит все равно откладывается

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }

    @Test
    fun `no one with IN_QUEUE status`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()

        val officeId = OFFICE_101
        queueManagerService.officeIsReady(officeId)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION)
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(checkingPId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.GOING_TO_OBSERVATION)
        ))))
    }

    @Test
    fun `patient is not in queue`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()

        val officeId = OFFICE_101
        queueManagerService.deleteFromQueue(checkingPId)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(checkingPId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))
    }

    @Test
    fun `patient has status !GOING_TO_OBSERVATION`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkingPSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkingPId = checkingPSr.first().subject!!.id()

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.IN_QUEUE)
        ))))

        //проверяемые действия
        queueManagerService.delayGoingToObservation(checkingPId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = checkingPId, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }
}