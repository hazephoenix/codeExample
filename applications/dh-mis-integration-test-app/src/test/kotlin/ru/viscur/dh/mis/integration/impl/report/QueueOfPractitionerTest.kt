package ru.viscur.dh.mis.integration.impl.report

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 12.11.2019 8:42 by SherbakovaMA
 *
 * Тест на информацию об очереди к врачу [ReportService.queueOfPractitioner]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class QueueOfPractitionerTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var examinationService: ExaminationService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var observationService: ObservationService

    @Autowired
    lateinit var observationInCarePlanService: ObservationInCarePlanService

    @Autowired
    lateinit var practitionerService: PractitionerService

    @Test
    fun test() {
        forTestService.cleanDb()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена

        val servReqsFromRegister = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
        ))
        val patientId = servReqsFromRegister.first().subject!!.id!!

        val practitionerId = Helpers.diagnosticAssistantId
        practitionerService.updateOnWork(practitionerId, true, OFFICE_101)


        forTestService.checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))

        forTestService.checkQueueForPractitioner(practitionerId, queueItems = listOf(
                QueueItemSimple(patientId = patientId)
        ), officeId = OFFICE_101)

        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = OFFICE_101, items = listOf(
                QueueItemSimple(patientId = patientId)
        ))))
        //проходит обследования в 101 кабинете
        val officeId = OFFICE_101
        queueManagerService.officeIsReady(officeId)
        val servReqs = queueManagerService.patientEntered(patientId, officeId)
        servReqs.forEach { servReq ->
            val servReqId = servReq.id
            observationService.start(servReqId)
            val observation = Helpers.createObservation(
                    basedOnServiceRequestId = servReqId,
                    status = ObservationStatus.final,
                    practitionerId = Helpers.diagnosticAssistantId
            )
            observationInCarePlanService.create(observation)
        }
        queueManagerService.patientLeft(patientId, officeId)

        //пациент ожидает в зеленой зоне
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = GREEN_ZONE, items = listOf(
                QueueItemSimple(patientId = patientId)
        ))))
        //теперь для работника в 101 кабинете нет очереди
        forTestService.checkQueueForPractitioner(practitionerId, queueItems = listOf(
        ), officeId = OFFICE_101)
        //для хирурга отображается пациент - он у него на отв.
        forTestService.checkQueueForPractitioner(Helpers.surgeonId, queueItems = listOf(
                QueueItemSimple(patientId = patientId)
        ))
        //для хирурга2 не отображается пациент
        forTestService.checkQueueForPractitioner(Helpers.surgeon2Id, queueItems = listOf(
        ))

        //добавили осмотр уролога
        examinationService.addServiceRequests(Bundle(entry = listOf(BundleEntry(Helpers.createServiceRequestResource(servRequestCode = INSPECTION_OF_UROLOGIST, patientId = patientId)))))
        //остался в зеленой зоне
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = GREEN_ZONE, items = listOf(
                QueueItemSimple(patientId = patientId)
        ))))
        forTestService.checkActiveServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestSimple(code = INSPECTION_OF_UROLOGIST, locationId = GREEN_ZONE),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        ))
        //для хирурга не отображается пациент, хоть и есть отв. осмотр, но его нельзя еще провести из-за осмотра уролога
        forTestService.checkQueueForPractitioner(Helpers.surgeonId, queueItems = listOf(
        ))
        //для любого уролога отображается пациент
        forTestService.checkQueueForPractitioner(Helpers.urologistId, queueItems = listOf(
                QueueItemSimple(patientId = patientId)
        ))
        forTestService.checkQueueForPractitioner(Helpers.urologist2Id, queueItems = listOf(
                QueueItemSimple(patientId = patientId)
        ))
    }

    @AfterEach
    fun after() {
        practitionerService.updateOnWork(Helpers.diagnosticAssistantId, false)
    }
}
