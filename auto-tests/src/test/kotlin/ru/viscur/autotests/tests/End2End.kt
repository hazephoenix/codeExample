package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient

class End2End {

    companion object {
        //фельдшер
        const val paramedicId = Helpers.paramedicId
        //кто делает все observation
        const val diagnosticAssistantId = Helpers.diagnosticAssistantId
        //ответсвенный
        const val respPractitionerId = Helpers.surgeonId
    }

    @Test
    @Order(1)
    fun patientE2e() {

        val patient = Helpers.createPatientResource(enp = "7879")
        val bodyWeight = Helpers.createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
        val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource()
        val personalDataConsent = Helpers.createConsentResource()
        val diagnosticReport = Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
        val list = Helpers.createPractitionerListResource(respPractitionerId)
        val claim = Helpers.createClaimResource()
        val servReq1 = Helpers.createServiceRequestResource("СтХир")

        val bundle = Bundle(entry = listOf(
                BundleEntry(patient),
                BundleEntry(diagnosticReport),
                BundleEntry(bodyWeight),
                BundleEntry(servReq1),
                BundleEntry(personalDataConsent),
                BundleEntry(list),
                BundleEntry(claim),
                BundleEntry(questionnaireResponseSeverityCriteria)
        ))
//        QueRequests.deleteQue()

        val office139Id = "Office:139"
        QueRequests.cabinetIsBusy(referenceToLocation(office139Id))
        val responseBundle = QueRequests.createPatient(bundle).extract().response().`as`(Bundle::class.java)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        //проверка что есть первый Service Request и он в нужный кабинет
        val actServiceRequest = responseBundle.entry.get(0).resource as ServiceRequest
        assertNotNull(actServiceRequest.subject?.id, "id patient")
        assertEquals(office139Id, actServiceRequest.locationReference?.first()?.id, "code ... ")

        val patientId = actServiceRequest.subject?.id!!
        assertEquals(servReq1.code.code(), actServiceRequest.code.code(), "code ... ")

        var actPatient = QueRequests.getResource(ResourceType.Patient.id.toString(), patientId).extract().response().`as`(Patient::class.java)
        assertEquals(PatientQueueStatus.IN_QUEUE, actPatient.extension.queueStatus, "wrong status of patient ... ")

        //queue items..
        val patientEnteredListResource = ListResource(entry = listOf(
                ListResourceEntry(item = referenceToPatient(patientId)),
                ListResourceEntry(item = referenceToLocation(office139Id))
        ))
        //кабинет READY
        QueRequests.officeIsReady(referenceToLocation(office139Id))
        //пациент вошел в кабинет
        val actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource).extract().response().`as`(Bundle::class.java)
                .let { it.entry.map { it.resource as ServiceRequest } }
        actPatient = QueRequests.getResource(ResourceType.Patient.id.toString(), patientId).extract().response().`as`(Patient::class.java)
        //проверка что в кабинете необходимые обследования и пациент
        assertEquals(1, actServicesInOffice.size, "wrong number of office's service requests")
        assertEquals(PatientQueueStatus.ON_OBSERVATION, actPatient.extension.queueStatus, "wrong patient status")

        //т к единственное обследование - осмотр отв. - завершение обследования:
        val actServiceInOffice = actServicesInOffice.first()
        assertEquals(servReq1.code.code(), actServiceInOffice.code.code(), "wrong service request code in office")
        assertEquals(patientId, actServiceInOffice.subject?.id, "wrong patientId of service request")

        val obsOfRespPract = Helpers.createObservation(code = actServiceInOffice.code.code(),
                valueString = "состояние удовлетворительное",
                practitionerId = actServiceInOffice.performer?.first()?.id!!,
                basedOnServiceRequestId = actServiceInOffice.id
        )
        val diagnosticReportOfResp = Helpers.createDiagnosticReportResource(
                diagnosisCode = "A00.0",
                practitionerId = respPractitionerId,
                status = DiagnosticReportStatus.final
        )
        val encounter = Helpers.createEncounter(hospitalizationStr = "Клиники СибГму")
        val bundleForExamination = Bundle(entry = listOf(
                BundleEntry(obsOfRespPract),
                BundleEntry(diagnosticReportOfResp),
                BundleEntry(encounter)
        ))
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination).extract().response().`as`(ClinicalImpression::class.java)
        assertEquals(ClinicalImpressionStatus.completed, completedClinicalImpression.status, "wrong status completed ClinicalImpression")
    }
}