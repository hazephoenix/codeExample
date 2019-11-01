package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createPatientResource
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.valueSets.BundleType

class QueueLogic {

    companion object {
        //фельдшер
        val paramedicId = Helpers.paramedicId
        //кто делает все observation
        val diagnosticAssistantId = Helpers.diagnosticAssistantId
        //ответсвенный
        val respPractitionerId = Helpers.surgeonId
    }

    @Test
    fun addingObservation() {

        /*val observation = Observation(
                status = ObservationStatus.final,
                issued = Timestamp.valueOf(LocalDateTime.now()),
                basedOn = Reference(
                        resourceType = ResourceType.ResourceTypeId.ServiceRequest,
                        id = "dd252a96-a5f1-4206-9e00-f0e9a0ec716a"
                ),
                performer = listOf(
                        Reference(
                                resourceType = ResourceType.ResourceTypeId.Practitioner, id = "80a3c463-3d68-4aa6-8020-de2a90c92962"
                        )
                ),
                code = CodeableConcept(
                        systemId = "ValueSet/Observation_types",
                        code = "B03.016.002ГМУ_СП"
                ),
                subject = Reference(
                        resourceType = ResourceType.ResourceTypeId.Patient, id = "c1d7e69a-3845-4a55-9c89-548d3a5c0114"
                ),
                valueString = "asdf"
        )
        QueRequests.createObservation(observation).log().all()*/
    }

    @Test
    fun patientsUziSorting() {
        val uziPatient1 = createPatientResource("1111")
        val uziPatient2 = createPatientResource("1112")
        val office116 = referenceToLocation("Office:116")
        val office117 = referenceToLocation("Office:117")
        // uziPatient1
        val bodyWeight1 = createObservation(code = "Weight", valueInt = 90)
        val questionnaireResponseSeverityCriteria1 = Helpers.createQuestResponseResource()
        val personalDataConsent1 = Helpers.createConsentResource()
        val diagnosticReport1 = Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = diagnosticAssistantId)
        val list1 = Helpers.createPractitionerListResource("хирург_Петров")
        val claim1 = Helpers.createClaimResource()
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle1 = Bundle(
                type = BundleType.BATCH.value,
                entry = listOf(
                        BundleEntry(uziPatient1),
                        BundleEntry(diagnosticReport1),
                        BundleEntry(bodyWeight1),
                        BundleEntry(servReq1),
                        BundleEntry(personalDataConsent1),
                        BundleEntry(list1),
                        BundleEntry(claim1),
                        BundleEntry(questionnaireResponseSeverityCriteria1)
                )
        )
        //uziPatient2
        val bodyWeight2 = createObservation(code = "Weight", valueInt = 90)
        val questionnaireResponseSeverityCriteria2 = Helpers.createQuestResponseResource()
        val personalDataConsent2 = Helpers.createConsentResource()
        val diagnosticReport2 = Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
        val list2 = Helpers.createPractitionerListResource(respPractitionerId)
        val claim2 = Helpers.createClaimResource()
        val servReq2 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle2 = Bundle(
                type = BundleType.BATCH.value,
                entry = listOf(
                        BundleEntry(uziPatient2),
                        BundleEntry(diagnosticReport2),
                        BundleEntry(bodyWeight2),
                        BundleEntry(servReq2),
                        BundleEntry(personalDataConsent2),
                        BundleEntry(list2),
                        BundleEntry(claim2),
                        BundleEntry(questionnaireResponseSeverityCriteria2)
                )
        )
        QueRequests.deleteQue()
        QueRequests.cabinetIsBusy(office116)
        QueRequests.cabinetIsBusy(office117)
        val servReqUzi1 = QueRequests.createPatient(bundle1).extract().response().`as`(Bundle::class.java).entry.first().resource as ServiceRequest
        val servReqUzi2 = QueRequests.createPatient(bundle2).extract().response().`as`(Bundle::class.java).entry.first().resource as ServiceRequest
        val queitem117 = QueRequests.getOfficeQue(office117).extract().response().`as`(Bundle::class.java).entry.first().resource as QueueItem
        val queitem116 = QueRequests.getOfficeQue(office116).extract().response().`as`(Bundle::class.java).entry.first().resource as QueueItem
        //проверка, что оба идут на обследование в разных кабинетах
        assertEquals(servReqUzi1.subject?.id, queitem117.subject.id, "wrong queitem for uziPatient1")
        assertEquals(servReqUzi2.subject?.id, queitem116.subject.id, "wrong queitem for uziPatient2")
    }

    @Test
    fun forceInvite() {
        val patient1 = createPatientResource("1113")
        val patient2 = createPatientResource("1112")
        // patient1
        val bodyWeight1 = createObservation(code = "Weight", valueInt = 90)
        val questionnaireResponseSeverityCriteria1 = Helpers.createQuestResponseResource()
        val personalDataConsent1 = Helpers.createConsentResource()
        val diagnosticReport1 = Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
        val list1 = Helpers.createPractitionerListResource(respPractitionerId)
        val claim1 = Helpers.createClaimResource()
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle1 = Bundle(
                type = BundleType.BATCH.value,
                entry = listOf(
                        BundleEntry(patient1),
                        BundleEntry(diagnosticReport1),
                        BundleEntry(bodyWeight1),
                        BundleEntry(servReq1),
                        BundleEntry(personalDataConsent1),
                        BundleEntry(list1),
                        BundleEntry(claim1),
                        BundleEntry(questionnaireResponseSeverityCriteria1)
                )
        )
        QueRequests.deleteQue()
        QueRequests.officeIsReady(referenceToLocation("Office:117"))
        val servReq = QueRequests.createPatient(bundle1).extract().response().`as`(Bundle::class.java).entry

    }
}