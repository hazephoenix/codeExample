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
import ru.viscur.dh.datastorage.api.util.OFFICE_101
import ru.viscur.dh.datastorage.api.util.OFFICE_119
import ru.viscur.dh.datastorage.api.util.OFFICE_202
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 15.11.2019 11:38 by SherbakovaMA
 *
 * Проверка изменений [ru.viscur.dh.fhir.model.type.LocationExtension.nextOfficeForPatientsInfo]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class NextOfficeForPatientsInfoTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var examinationService: ExaminationService

    @Autowired
    lateinit var receptionService: ReceptionService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var observationService: ObservationService

    @Autowired
    lateinit var patientService: PatientService

    @Test
    fun `patient left office`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        patientDoneSingleServiceRequestInOffice(officeId, pId1, p1)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
                NextOfficeForPatientInfoSimple(patientId = pId1, nextOfficeId = OFFICE_202)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, items = listOf(
                QueueItemSimple(patientId = pId1)
        ))))
    }

    @Test
    fun `patient left office, second in nextOfficeForPatientsInfo`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!
        val p2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId2 = p2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1),
                QueueItemSimple(patientId = pId2)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        patientDoneSingleServiceRequestInOffice(officeId, pId1, p1)
        patientDoneSingleServiceRequestInOffice(officeId, pId2, p2)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
                NextOfficeForPatientInfoSimple(patientId = pId1, nextOfficeId = OFFICE_202),
                NextOfficeForPatientInfoSimple(patientId = pId2, nextOfficeId = OFFICE_202)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, items = listOf(
                QueueItemSimple(patientId = pId1),
                QueueItemSimple(patientId = pId2)
        ))))
    }

    @Test
    fun `patient is deleted from queue, should not be shown in info`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        queueManagerService.deleteFromQueue(pId1)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
        ))))
    }

    @Test
    fun `patient is force sent, should be shown in info`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        queueManagerService.forceSendPatientToObservation(pId1, OFFICE_202)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
                NextOfficeForPatientInfoSimple(patientId = pId1, nextOfficeId = OFFICE_202)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, officeStatus = LocationStatus.WAITING_PATIENT, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.GOING_TO_OBSERVATION)
        ))))
    }

    @Test
    fun `patient set first to another office, should be shown in info`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        queueManagerService.setAsFirst(pId1, OFFICE_202)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
                NextOfficeForPatientInfoSimple(patientId = pId1, nextOfficeId = OFFICE_202)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }

    @Test
    fun `patient set first to the same office, should not be shown in info`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!
        val p2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId2 = p2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1),
                QueueItemSimple(patientId = pId2)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        queueManagerService.setAsFirst(pId2, officeId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = pId2),
                QueueItemSimple(patientId = pId1)
        ), nextOfficeForPatientsInfo = listOf(
        ))))
    }

    @Test
    fun `cancel service request by officeId, should be shown in info`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1)
        ), nextOfficeForPatientsInfo = listOf(
        ))))

        //проверяемые действия
        examinationService.cancelServiceRequests(pId1, officeId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
                NextOfficeForPatientInfoSimple(patientId = pId1, nextOfficeId = OFFICE_202)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.IN_QUEUE)
        ))))
    }

    @Test
    fun `patient entered to office, should delete info from prev office`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val p1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val pId1 = p1.first().subject!!.id!!

        var officeId = OFFICE_101
        patientDoneSingleServiceRequestInOffice(officeId, pId1, p1)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
                NextOfficeForPatientInfoSimple(patientId = pId1, nextOfficeId = OFFICE_202)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, items = listOf(
                QueueItemSimple(patientId = pId1)
        ))))

        //проверяемые действия
        officeId = OFFICE_202
        queueManagerService.officeIsReady(officeId)
        queueManagerService.patientEntered(pId1, officeId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = OFFICE_101, items = listOf(
        ), nextOfficeForPatientsInfo = listOf(
        )), QueueOfOfficeSimple(officeId = OFFICE_202, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = pId1, status = PatientQueueStatus.ON_OBSERVATION)
        ), nextOfficeForPatientsInfo = listOf(
        ))))
    }

    private fun patientDoneSingleServiceRequestInOffice(officeId: String, patientId: String, servReqs: List<ServiceRequest>) {
        queueManagerService.officeIsReady(officeId)
        queueManagerService.patientEntered(patientId, officeId)
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        val severity = patientService.severity(patientId)
        val observation = Helpers.createObservation(basedOnServiceRequestId = servReqs.first().id)
        observationService.create(patientId, observation, diagnosis, severity)
        queueManagerService.patientLeft(patientId, officeId)
    }
}