package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.*
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class Examinations {

    companion object {
        val office101 = "Office:101"
        val redZone = "Office:RedZone"
        val observationOfSurgeonCode = "СтХир"
        val observation1 = "A04.16.001"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingExamination() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val serviceRequest = responseBundle.resources(ResourceType.ServiceRequest).first()
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        val obsOfRespPract = Helpers.createObservation(code = serviceRequest.code.code(),
                valueString = "состояние удовлетворительное",
                practitionerId = serviceRequest.performer?.first()?.id!!,
                basedOnServiceRequestId = serviceRequest.id,
                status = ObservationStatus.final,
                patientId = patientId
        )
        //завершение обращение пациента отвественным
        val diagnosticReportOfResp = Helpers.createDiagnosticReportResource(
                diagnosisCode = "A00.0",
                practitionerId = Helpers.surgeonId,
                status = DiagnosticReportStatus.final,
                patientId = patientId
        )
        val encounter = Helpers.createEncounter(hospitalizationStr = "Клиники СибГму", patientId = patientId)
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
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observation1)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val serviceRequest = responseBundle.resources(ResourceType.ServiceRequest).first()
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        //завершение обращения с активным Service Request
        val obsOfRespPract = Helpers.createObservation(code = serviceRequest.code.code(),
                valueString = "состояние удовлетворительное",
                practitionerId = Helpers.surgeonId,
                basedOnServiceRequestId = serviceRequest.id,
                status = ObservationStatus.final,
                patientId = patientId
        )
        val diagnosticReportOfResp = Helpers.createDiagnosticReportResource(
                diagnosisCode = "A00.0",
                practitionerId = Helpers.surgeonId,
                status = DiagnosticReportStatus.final,
                patientId = patientId
        )
        val encounter = Helpers.createEncounter(hospitalizationStr = "Клиники СибГму", patientId = patientId)
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
        val observation = "B03.016.002"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observation)
        )
        val bundle = Helpers.bundle("7879", "RED", servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        //проверка наличия активных Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observationOfSurgeonCode,
                        locationId = redZone,
                        status = ServiceRequestStatus.active
                ),
                ServiceRequestInfo(
                        code = observation,
                        locationId = office101,
                        status = ServiceRequestStatus.active
                )
        ))
        //отмена обращения
        QueRequests.cancelExamination(patientId)
        //проверка, что обращение отменено и больше нет активных Service Request
        checkServiceRequestsOfPatient(patientId, listOf())
    }
}