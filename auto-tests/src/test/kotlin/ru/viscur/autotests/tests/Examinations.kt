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
        val office139 = "Office:139"
        val office101 = "Office:101"
        val observationOfSurgeonCode = "СтХир"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingExamination() {
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
                status = ObservationStatus.final
        )
        //завершаем обращение пациента отвественным и проверяем отсутствие Service Requests, Observation
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
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)
        Assertions.assertEquals(ClinicalImpressionStatus.completed, completedClinicalImpression.status, "wrong status completed ClinicalImpression")
        checkServiceRequestsOfPatient(patientId, listOf())
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    //Todo написать
    fun addingExaminationWithActiveObservation() {

    }

    @Test
    fun cancelingClinicalImpression() {
        val observation = "B03.016.002ГМУ_СП"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observation)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observationOfSurgeonCode,
                        locationId = office139
                ),
                ServiceRequestInfo(
                        code = observation,
                        locationId = office101
                )
        ))

        QueRequests.cancelExamination(patientId).log().all()
        checkQueueItems(listOf())
        checkServiceRequestsOfPatient(patientId, listOf())
    }


}