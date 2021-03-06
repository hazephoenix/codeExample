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
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 08.11.2019 14:27 by SherbakovaMA
 *
 * Проверка изменения степени тяжести (с перестановкой в очереди не меняя кабинет)
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class UpdateSeverityTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var examinationService: ExaminationService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var patientService: PatientService

    @Test
    fun testRedToGreen() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(true)
        var i = 0
        val red1 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = forTestService.createPatientWithQueueItem(severity = Severity.RED, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(Severity.RED, patientService.severity(checking))
        assertEquals(Severity.RED.display.substring(0, 1) + "-", patientService.queueCode(checking).substring(0, 2))

        //поменяли на зеленый, должен встать в конец,
        // не меняя кабинета, хоть и рядом пустой
        examinationService.updateSeverity(checking, Severity.GREEN)
        assertEquals(Severity.GREEN, patientService.severity(checking))
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2),
                QueueItemSimple(patientId = checking)
        ))))
        assertEquals(Severity.GREEN.display.substring(0, 1) + "-", patientService.queueCode(checking).substring(0, 2))
    }

    @Test
    fun testRedToYellow() {
        forTestService.cleanDb()
        var i = 0
        val red1 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = forTestService.createPatientWithQueueItem(severity = Severity.RED, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(Severity.RED, patientService.severity(checking))
        assertEquals(Severity.RED.display.substring(0, 1) + "-", patientService.queueCode(checking).substring(0, 2))

        //поменяли на желтый, должен встать после желтых
        examinationService.updateSeverity(checking, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(checking))
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(Severity.YELLOW.display.substring(0, 1) + "-", patientService.queueCode(checking).substring(0, 2))
    }

    @Test
    fun testRedToRed() {
        forTestService.cleanDb()
        var i = 0
        val checking = forTestService.createPatientWithQueueItem(severity = Severity.RED, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val red1 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(Severity.RED, patientService.severity(checking))
        val queueCodeBefore = patientService.queueCode(checking)
        assertEquals(Severity.RED.display.substring(0, 1) + "-", queueCodeBefore.substring(0, 2))

        //ничего не должно поменяться - с красного на красный
        examinationService.updateSeverity(checking, Severity.RED)
        assertEquals(Severity.RED, patientService.severity(checking))
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(queueCodeBefore, patientService.queueCode(checking))
    }

    @Test
    fun testGreenToRed() {
        forTestService.cleanDb()
        var i = 0
        val red1 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val red2 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(Severity.GREEN, patientService.severity(checking))
        assertEquals(Severity.GREEN.display.substring(0, 1) + "-", patientService.queueCode(checking).substring(0, 2))

        //после красных
        examinationService.updateSeverity(checking, Severity.RED)
        assertEquals(Severity.RED, patientService.severity(checking))
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre2)
        ))))
        assertEquals(Severity.RED.display.substring(0, 1) + "-", patientService.queueCode(checking).substring(0, 2))
    }

    @Test
    fun testGreenToYellow() {
        forTestService.cleanDb()
        var i = 0
        val red1 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val red2 = forTestService.createPatientWithQueueItem(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = forTestService.createPatientWithQueueItem(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = forTestService.createPatientWithQueueItem(severity = Severity.GREEN, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        forTestService.updateOfficeStatuses()
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = checking)
        ))))
        assertEquals(Severity.GREEN, patientService.severity(checking))

        //после желтых
        examinationService.updateSeverity(checking, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(checking))
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = gre1)
        ))))
    }

    @Test
    fun testGreenToYellowWithoutQueue() {
        forTestService.cleanDb()
        val checking = forTestService.createPatient(severity = Severity.GREEN, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130)
        forTestService.updateOfficeStatuses()
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
        ))))
        assertEquals(Severity.GREEN, patientService.severity(checking))

        examinationService.updateSeverity(checking, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(checking))
    }

    @Test
    fun testZoneGreenToYellowWithoutQueue() {
        forTestService.cleanDb()
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val servReqsFromRegister = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val patientId = servReqsFromRegister.first().subject!!.id()
        forTestService.checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_130, locationId = OFFICE_130),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))
        assertEquals(Severity.GREEN, patientService.severity(patientId))

        examinationService.updateSeverity(patientId, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(patientId))
        forTestService.checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_130, locationId = OFFICE_130),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = YELLOW_ZONE_SECTION_1)
        ))
    }
}