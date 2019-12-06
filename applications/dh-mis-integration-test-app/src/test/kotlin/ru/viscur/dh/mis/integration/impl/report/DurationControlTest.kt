package ru.viscur.dh.mis.integration.impl.report

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
import ru.viscur.dh.fhir.model.enums.DiagnosticReportStatus
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.integration.mis.api.ExaminationService
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 12.11.2019 8:42 by SherbakovaMA
 *
 * Тест на сохранение времени проведения услуг и времени нахождения в очереди (на опр. этапах)
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class DurationControlTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var examinationService: ExaminationService

    @Autowired
    lateinit var receptionService: ReceptionService

    @Autowired
    lateinit var reportService: ReportService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var observationService: ObservationService

    @Autowired
    lateinit var observationInCarePlanService: ObservationInCarePlanService

    @Autowired
    lateinit var patientService: PatientService

    @Autowired
    lateinit var durationEstimationService: ObservationDurationEstimationService

    @Test
    fun test() {
        forTestService.cleanDb()
        durationEstimationService.deleteAllHistory()
        queueManagerService.recalcNextOffice(false)
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена
        val servReqsFromRegister = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val patientId = servReqsFromRegister.first().subject!!.id()

        listOf(OFFICE_101, OFFICE_202).forEach { officeId ->
            Thread.sleep(1000)
            queueManagerService.officeIsReady(officeId)
            Thread.sleep(1000)
            val servReqs = queueManagerService.patientEntered(patientId, officeId)

            servReqs.forEach { servReq ->
                val servReqId = servReq.id
                observationService.start(servReqId)
                //время выполнения 3 сек
                Thread.sleep(3000)
                val observation = Helpers.createObservation(
                        basedOnServiceRequestId = servReqId,
                        status = ObservationStatus.final,
                        practitionerId = Helpers.diagnosticAssistantId
                )
                observationInCarePlanService.create(observation)
            }
            queueManagerService.patientLeft(patientId, officeId)
        }
        val officeId = OFFICE_139
        queueManagerService.officeIsReady(officeId)
        queueManagerService.forceSendPatientToObservation(patientId, officeId)
        val servReqId = queueManagerService.patientEntered(patientId, officeId).first().id
        observationService.start(servReqId)
        Thread.sleep(3000)

        val obsOfRespPract = Helpers.createObservation(
                valueString = "состояние удовлетворительное",
                practitionerId = Helpers.surgeonId,
                basedOnServiceRequestId = servReqId,
                status = ObservationStatus.final
        )
        val diagnosticReportOfResp = Helpers.createDiagnosticReportResource(
                diagnosisCode = "A00.0",
                practitionerId = Helpers.surgeonId,
                status = DiagnosticReportStatus.final
        )
        val encounter = Helpers.createEncounter(hospitalizationStr = "Клиники СибГму")
        val bundleForExamination = Bundle(entry = listOf(
                BundleEntry(obsOfRespPract),
                BundleEntry(diagnosticReportOfResp),
                BundleEntry(encounter)
        ))
        examinationService.completeExamination(bundleForExamination)

        //проверка после
        forTestService.checkObsDuration(patientId, listOf(
                ObservationDurationSimple(duration = 120, code = INSPECTION_ON_RECEPTION),
                ObservationDurationSimple(duration = 132, code = CLINICAL_IMPRESSION),
                ObservationDurationSimple(duration = 3, code = OBSERVATION_IN_OFFICE_101),
                ObservationDurationSimple(duration = 3, code = OBSERVATION2_IN_OFFICE_101),
                ObservationDurationSimple(duration = 3, code = OBSERVATION_IN_OFFICE_202),
                ObservationDurationSimple(duration = 3, code = OBSERVATION_OF_SURGEON)
        ))
        forTestService.checkQueueHistoryOfPatient(patientId, listOf(
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.READY),
                QueueHistoryOfPatientSimple(duration = 1, status = PatientQueueStatus.IN_QUEUE, officeId = OFFICE_101),
                QueueHistoryOfPatientSimple(duration = 1, status = PatientQueueStatus.GOING_TO_OBSERVATION, officeId = OFFICE_101),
                QueueHistoryOfPatientSimple(duration = 6, status = PatientQueueStatus.ON_OBSERVATION, officeId = OFFICE_101),
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.READY),
                QueueHistoryOfPatientSimple(duration = 1, status = PatientQueueStatus.IN_QUEUE, officeId = OFFICE_202),
                QueueHistoryOfPatientSimple(duration = 1, status = PatientQueueStatus.GOING_TO_OBSERVATION, officeId = OFFICE_202),
                QueueHistoryOfPatientSimple(duration = 3, status = PatientQueueStatus.ON_OBSERVATION, officeId = OFFICE_202),
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.READY),
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.IN_QUEUE, officeId = GREEN_ZONE),
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.READY),
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.IN_QUEUE, officeId = OFFICE_139),
                QueueHistoryOfPatientSimple(duration = 0, status = PatientQueueStatus.GOING_TO_OBSERVATION, officeId = OFFICE_139),
                QueueHistoryOfPatientSimple(duration = 3, status = PatientQueueStatus.ON_OBSERVATION, officeId = OFFICE_139)
        ))
    }
}