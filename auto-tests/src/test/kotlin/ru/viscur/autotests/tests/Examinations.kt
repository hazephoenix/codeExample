package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.tests.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.tests.Constants.Companion.office101Id
import ru.viscur.autotests.tests.Constants.Companion.redZoneId
import ru.viscur.autotests.utils.*
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createDiagnosticReportResource
import ru.viscur.autotests.utils.Helpers.Companion.createEncounter
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.Helpers.Companion.surgeonId
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class Examinations {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingExamination() {
        //создание пациента
        val bundle = bundle("7879", Severity.RED.toString())
        val responseBundle = QueRequests.createPatient(bundle)
        val serviceRequest = responseBundle.resources(ResourceType.ServiceRequest).first()
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        val obsOfRespPract = createObservation(code = serviceRequest.code.code(),
                valueString = "состояние удовлетворительное",
                practitionerId = serviceRequest.performer?.first()?.id!!,
                basedOnServiceRequestId = serviceRequest.id,
                status = ObservationStatus.final,
                patientId = patientId
        )

        //завершение обращение пациента отвественным
        val diagnosticReportOfResp = createDiagnosticReportResource(
                diagnosisCode = "A16",
                practitionerId = surgeonId,
                status = DiagnosticReportStatus.final,
                patientId = patientId
        )
        val encounter = createEncounter(hospitalizationStr = "Клиники СибГму", patientId = patientId)
        val bundleForExamination = Bundle(entry = listOf(
                BundleEntry(obsOfRespPract),
                BundleEntry(diagnosticReportOfResp),
                BundleEntry(encounter)
        ))
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)

        //проверка отсутствия Service Requests, Observation
        Assertions.assertEquals(ClinicalImpressionStatus.completed, completedClinicalImpression.status, "wrong status completed ClinicalImpression")
        checkServiceRequestsOfPatient(patientId, listOf())
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    fun addingExaminationWithActiveObservation() {
        //создание пациента
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle = bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val serviceRequest = responseBundle.resources(ResourceType.ServiceRequest).first()
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        //завершение обращения с активным Service Request
        val obsOfRespPract = createObservation(code = serviceRequest.code.code(),
                valueString = "состояние удовлетворительное",
                practitionerId = surgeonId,
                basedOnServiceRequestId = serviceRequest.id,
                status = ObservationStatus.final,
                patientId = patientId
        )
        val diagnosticReportOfResp = createDiagnosticReportResource(
                diagnosisCode = "A16",
                practitionerId = surgeonId,
                status = DiagnosticReportStatus.final,
                patientId = patientId
        )
        val encounter = createEncounter(hospitalizationStr = "Клиники СибГму", patientId = patientId)
        val bundleForExamination = Bundle(entry = listOf(
                BundleEntry(obsOfRespPract),
                BundleEntry(diagnosticReportOfResp),
                BundleEntry(encounter)
        ))
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)

        //проверка, что обращение завершено и больше нет активных ServiceRequests
        checkServiceRequestsOfPatient(patientId, listOf())
    }

    @Test
    fun cancelingClinicalImpression() {
        //создание пациента
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle = bundle("7879", "RED", servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        //проверка наличия активных Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observationOfSurgeon,
                        locationId = redZoneId,
                        status = ServiceRequestStatus.active
                ),
                ServiceRequestInfo(
                        code = observation1Office101,
                        locationId = office101Id,
                        status = ServiceRequestStatus.active
                )
        ))

        //отмена обращения
        QueRequests.cancelExamination(patientId)

        //проверка, что обращение отменено и больше нет активных Service Request
        checkServiceRequestsOfPatient(patientId, listOf())
    }
}