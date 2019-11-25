package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.util.GREEN_ZONE
import ru.viscur.dh.datastorage.api.util.OFFICE_101
import ru.viscur.dh.datastorage.api.util.OFFICE_104
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 09.11.2019 10:50 by SherbakovaMA
 *
 * Тест на метод "Регистрация обращения пациента" [ReceptionService.registerPatient]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class CreateOrUpdateObservationTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var observationInCarePlanService: ObservationInCarePlanService

    @Autowired
    lateinit var patientService: PatientService

    //    @Test todo не актуально
    fun `auto creating urine observation`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        queueManagerService.officeIsReady(officeId)
        queueManagerService.patientEntered(checkP, officeId)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = checkP, status = PatientQueueStatus.ON_OBSERVATION),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104, locationId = OFFICE_104),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))

        //проверяемые действия
        val observation = Helpers.createObservation(basedOnServiceRequestId = checkSr.first().id)
        observationInCarePlanService.create(observation)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = checkP, status = PatientQueueStatus.ON_OBSERVATION),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104, locationId = OFFICE_104, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))

        //проводим второе обсл.
        observationInCarePlanService.create(Helpers.createObservation(basedOnServiceRequestId = checkSr[1].id))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104, locationId = OFFICE_104, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))
    }

    @Test
    fun `ignoring in queue urine observation`() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104)
        ))
        val checkP = checkSr.first().subject!!.id!!
        val servReqs2 = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val p2 = servReqs2.first().subject!!.id!!

        val officeId = OFFICE_101

        queueManagerService.officeIsReady(officeId)
        queueManagerService.patientEntered(checkP, officeId)

        //проверка до
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.OBSERVATION, items = listOf(
                QueueItemSimple(patientId = checkP, status = PatientQueueStatus.ON_OBSERVATION),
                QueueItemSimple(patientId = p2)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104, locationId = OFFICE_104),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))

        //проверяемые действия
        val observation = Helpers.createObservation(basedOnServiceRequestId = checkSr.find { it.code.code() == OBSERVATION_IN_OFFICE_101 }!!.id)
        observationInCarePlanService.create(observation)
        queueManagerService.patientLeft(checkP, officeId)

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = p2)
        )),QueueOfOfficeSimple(officeId = GREEN_ZONE, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = checkP)
        ))))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104, locationId = OFFICE_104),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))

        //проводим обсл. мочи
        observationInCarePlanService.create(Helpers.createObservation(basedOnServiceRequestId = checkSr.find{ it.code.code() == OBSERVATION_IN_OFFICE_104 }!!.id))
        forTestService.checkServiceRequestsOfPatient(checkP, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104, locationId = OFFICE_104, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101, status = ServiceRequestStatus.waiting_result),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))
    }
}