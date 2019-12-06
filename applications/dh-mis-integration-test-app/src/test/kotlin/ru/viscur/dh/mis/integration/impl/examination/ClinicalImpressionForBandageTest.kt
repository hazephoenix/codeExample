package ru.viscur.dh.mis.integration.impl.examination

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
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 22.11.2019 17:19 by SherbakovaMA
 *
 * Тест на полоное прохождение обращения по перевязке
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class ClinicalImpressionForBandageTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var observationService: ObservationService

    @Autowired
    lateinit var patientService: PatientService

    @Autowired
    lateinit var observationInCarePlanService: ObservationInCarePlanService

    @Autowired
    lateinit var receptionService: ReceptionService

    @Test
    fun test() {
        forTestService.cleanDb()
        val servReqs = forTestService.registerPatientForBandage()
        val patientId = servReqs.first().subject!!.id()

        val officeId = OFFICE_128
        forTestService.checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestSimple(code = BANDAGE, locationId = officeId)
        ))

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = patientId)
        ))))

        queueManagerService.officeIsReady(officeId)
        val srInOffice128 = queueManagerService.patientEntered(patientId, officeId)

        forTestService.checkServiceRequests(patientId, listOf(
                ServiceRequestSimple(code = BANDAGE, locationId = officeId)
        ), srInOffice128)

        observationInCarePlanService.create(Helpers.createObservation(basedOnServiceRequestId = srInOffice128.first().id, status = ObservationStatus.final))

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
        ))))

    }
}