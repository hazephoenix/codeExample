package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.ObservationService
import ru.viscur.dh.datastorage.api.util.OFFICE_101
import ru.viscur.dh.datastorage.api.util.OFFICE_130
import ru.viscur.dh.datastorage.api.util.OFFICE_139
import ru.viscur.dh.datastorage.api.util.OFFICE_202
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 09.11.2019 10:50 by SherbakovaMA
 *
 * Тест на методы "Отменить назначение(-я) пациента" [ExaminationService.cancelServiceRequest] и [ExaminationService.cancelServiceRequests]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
//@Disabled("Debug purposes only. Test cleans and modifies db")
class CancelServiceRequestsTest {

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

    @Test
    fun `cancel by officeId, in queue`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val servReqs1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p1 = servReqs1.first().subject!!.id!!
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))

        //проверяемые действия
        examinationService.cancelServiceRequests(checkP, officeId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = p2)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, items = listOf(
                QueueItemSimple(patientId = checkP)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.cancelled),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.cancelled),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))
    }

    @Test
    fun `cancel by officeId, not in office queue`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val servReqs1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p1 = servReqs1.first().subject!!.id!!
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))

        //проверяемые действия
        examinationService.cancelServiceRequests(checkP, OFFICE_202)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202, status = ServiceRequestStatus.cancelled),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))
    }

    @Test
    fun `cancel by officeId, wrong officeId`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val servReqs1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p1 = servReqs1.first().subject!!.id!!
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))

        //проверяемые действия
        examinationService.cancelServiceRequests(checkP, OFFICE_130)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))
    }

    @Test
    fun `cancel by id, single servReq in office`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val servReqs1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p1 = servReqs1.first().subject!!.id!!
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))

        //проверяемые действия
        examinationService.cancelServiceRequest(checkSr.first().id)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = p2)
        )), QueueOfOfficeSimple(officeId = OFFICE_202, items = listOf(
                QueueItemSimple(patientId = checkP)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.cancelled),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))
    }

    @Test
    fun `cancel by id, multiple servReqs in office`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val servReqs1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p1 = servReqs1.first().subject!!.id!!
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))

        //проверяемые действия
        examinationService.cancelServiceRequest(checkSr.first().id)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.cancelled),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))
    }

    @Test
    fun `cancel by id, multiple servReqs in office with waiting result`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val servReqs1 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p1 = servReqs1.first().subject!!.id!!
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))

        //проверяемые действия
        queueManagerService.officeIsReady(officeId)
        queueManagerService.patientEntered(checkP, officeId)

        examinationService.cancelServiceRequest(checkSr.first().id)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, items = listOf(
                QueueItemSimple(patientId = p1),
                QueueItemSimple(patientId = checkP),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.cancelled),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = OFFICE_139)
        ))
    }
}