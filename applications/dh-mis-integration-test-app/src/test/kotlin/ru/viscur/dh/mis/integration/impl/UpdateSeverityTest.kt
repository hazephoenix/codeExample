package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.util.OFFICE_130
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
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
class UpdateSeverityTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var examinationService: ExaminationService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var resourceService: ResourceService

    @Autowired
    lateinit var patientService: PatientService

    @Test
    fun testRedToGreen() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(true)
        var i = 0
        val red1 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = createPatient(severity = Severity.RED, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
        assertEquals(Severity.RED, patientService.severity(checking))

        //поменяли на зеленый, должен встать в конец,
        // не меняя кабинета, хоть и рядом пустой
        examinationService.updateSeverity(checking, Severity.GREEN)
        assertEquals(Severity.GREEN, patientService.severity(checking))
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2),
                QueueItemSimple(patientId = checking)
        ))), queueManagerService.queueItems())
    }

    @Test
    fun testRedToYellow() {
        forTestService.cleanDb()
        var i = 0
        val red1 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = createPatient(severity = Severity.RED, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
        assertEquals(Severity.RED, patientService.severity(checking))

        //поменяли на желтый, должен встать после желтых
        examinationService.updateSeverity(checking, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(checking))
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
    }

    @Test
    fun testRedToRed() {
        forTestService.cleanDb()
        var i = 0
        val checking = createPatient(severity = Severity.RED, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val red1 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
        assertEquals(Severity.RED, patientService.severity(checking))

        //ничего не должно поменяться - с красного на красный
        examinationService.updateSeverity(checking, Severity.RED)
        assertEquals(Severity.RED, patientService.severity(checking))
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
    }

    @Test
    fun testGreenToRed() {
        forTestService.cleanDb()
        var i = 0
        val red1 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val red2 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = createPatient(severity = Severity.GREEN, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre2 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
        assertEquals(Severity.GREEN, patientService.severity(checking))

        //после красных
        examinationService.updateSeverity(checking, Severity.RED)
        assertEquals(Severity.RED, patientService.severity(checking))
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre2)
        ))), queueManagerService.queueItems())
    }

    @Test
    fun testGreenToYellow() {
        forTestService.cleanDb()
        var i = 0
        val red1 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val red2 = createPatient(severity = Severity.RED, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel1 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val yel2 = createPatient(severity = Severity.YELLOW, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val gre1 = createPatient(severity = Severity.GREEN, officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        val checking = createPatient(severity = Severity.GREEN, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130, queueStatus = PatientQueueStatus.IN_QUEUE, index = i++)
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = gre1),
                QueueItemSimple(patientId = checking)
        ))), queueManagerService.queueItems())
        assertEquals(Severity.GREEN, patientService.severity(checking))

        //после желтых
        examinationService.updateSeverity(checking, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(checking))
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
                QueueItemSimple(patientId = red1),
                QueueItemSimple(patientId = red2),
                QueueItemSimple(patientId = yel1),
                QueueItemSimple(patientId = yel2),
                QueueItemSimple(patientId = checking),
                QueueItemSimple(patientId = gre1)
        ))), queueManagerService.queueItems())
    }

    @Test
    fun testGreenToYellowWithoutQueue() {
        forTestService.cleanDb()
        val checking = forTestService.createPatient(severity = Severity.GREEN, servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ), officeId = OFFICE_130)
        checkQueueItems(listOf(QueueOfOfficeSimple(OFFICE_130, listOf(
        ))), queueManagerService.queueItems())
        assertEquals(Severity.GREEN, patientService.severity(checking))

        examinationService.updateSeverity(checking, Severity.YELLOW)
        assertEquals(Severity.YELLOW, patientService.severity(checking))
    }

    private fun createPatient(severity: Severity, servReqs: List<ServiceRequestSimple>? = null, officeId: String, queueStatus: PatientQueueStatus, index: Int): String {
        val patientId = forTestService.createPatient(severity = severity, officeId = officeId, queueStatus = queueStatus, servReqs = servReqs)
        resourceService.create(QueueItem(
                onum = index,
                subject = referenceToPatient(patientId),
                estDuration = 5 * SECONDS_IN_MINUTE,
                location = referenceToLocation(officeId)
        ))
        return patientId
    }
}