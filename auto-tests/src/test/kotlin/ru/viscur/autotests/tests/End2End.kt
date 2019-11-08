package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.*
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*

@Disabled("Debug purposes only")
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
        QueRequests.deleteQue()
        val observationOfSurgeonCode = "СтХир"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val office139Id = "Office:139"

        QueRequests.officeIsBusy(referenceToLocation(office139Id))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        val serviceRequestsFromResponse = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequestsFromResponse)

        //проверка Service Request-ов из ответа
        compareServiceRequests(patientId, listOf(ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139Id)), serviceRequestsFromResponse)

        //проверка состояний в базе: состояние очереди, назначения, обследования
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        checkServiceRequestsOfPatient(patientId, listOf(ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139Id)))
        checkObservationsOfPatient(patientId, listOf())
        checkPatientsOfResp(listOf(
                PatientsOfRespInfo("фельдшер_Колосова", listOf(
                        PatientOfRespInfo(patientId, Severity.RED)
                ))
        ))

        //кабинет READY
        QueRequests.officeIsReady(referenceToLocation(office139Id))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //пациент вошел в кабинет
        val patientEnteredListResource = Helpers.createListResource(patientId, office139Id)
        val actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)

        checkQueueItems(listOf(
                QueueItemsOfOffice(office139Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val actPatient = QueRequests.resource(ResourceType.Patient, patientId)

        //проверка что в кабинете необходимые обследования и пациент
        compareServiceRequests(patientId, listOf(ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139Id)), serviceRequestsFromResponse)

        //т к единственное обследование - осмотр отв. - завершение обследования:
        val actServiceInOffice = actServicesInOffice.first()
        val obsOfRespPract = Helpers.createObservation(code = actServiceInOffice.code.code(),
                valueString = "состояние удовлетворительное",
                practitionerId = actServiceInOffice.performer?.first()?.id!!,
                basedOnServiceRequestId = actServiceInOffice.id,
                status = ObservationStatus.final
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
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)
        assertEquals(ClinicalImpressionStatus.completed, completedClinicalImpression.status, "wrong status completed ClinicalImpression")
        checkQueueItems(listOf())
        checkServiceRequestsOfPatient(patientId, listOf())
        checkObservationsOfPatient(patientId, listOf())
    }

    private fun patientIdFromServiceRequests(serviceRequestsFromResponse: List<ServiceRequest>): String {
        assertTrue(serviceRequestsFromResponse.size > 0, "list of service requests can't be empty")
        assertNotNull(serviceRequestsFromResponse.first().subject?.id, "wrong id patient")
        val patientId = serviceRequestsFromResponse.first().subject?.id!!
        return patientId
    }

    @Test
    @Order(1)
    fun patientAdditionalServRequestAdding() {
        QueRequests.deleteQue()
        val servRequests = listOf(
                Helpers.createServiceRequestResource("СтХир")
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val office139 = "Office:139"
        val office104 = "Office:104"
        val office116 = "Office:116"
        QueRequests.officeIsBusy(referenceToLocation(office139))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        val actServiceRequests = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServiceRequests)
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        var actPatient = QueRequests.resource(ResourceType.Patient, patientId)
        assertEquals(PatientQueueStatus.IN_QUEUE, actPatient.extension.queueStatus, "wrong status of patient ... ")

        //пациент вошел в кабинет
        QueRequests.officeIsReady(referenceToLocation(office139))
        val patientEnteredListResource = Helpers.createListResource(patientId, office139)
        val actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)
        actPatient = QueRequests.resource(ResourceType.Patient, patientId)

        //проверка что в кабинете необходимые обследования и пациент
        assertEquals(1, actServicesInOffice.size, "wrong number of office's service requests")
        assertEquals(PatientQueueStatus.ON_OBSERVATION, actPatient.extension.queueStatus, "wrong patient status")

        val additionalServiceRequests = listOf(
                Helpers.createServiceRequestResource("A04.16.001", patientId),
                Helpers.createServiceRequestResource("B03.016.006ГМУ_СП", patientId)
        )

        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        QueRequests.officeIsBusy(referenceToLocation(office104))
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //в CarePlan должны быть добавленные ServiceRequests и осмотр ответственного, пациент должен встать в очередь в другой кабинет
        assertEquals(3, updatedCarePlan.activity.size, "wrong care plan activities")
        checkQueueItems(listOf(
                QueueItemsOfOffice(office104, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "A04.16.001", locationId = office116),
                ServiceRequestInfo(code = "B03.016.006ГМУ_СП", locationId = office104)
        ))
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    @Order(1)
    fun fullPositiveE2ePatient() {
        QueRequests.deleteQue()
        val patientServiceRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.004ГМУ_СП"),
                Helpers.createServiceRequestResource("A09.20.003ГМУ_СП"),
                Helpers.createServiceRequestResource("A04.16.001")
        )
        val patientBundle = Helpers.bundle("1001", "RED", patientServiceRequests)
        val responseBundle = QueRequests.createPatient(patientBundle)
        assertEquals(4, responseBundle.entry.size, "wrong number of service request")

    }

    @Test
    @Order(1)
    fun fullPositiveE2ePatient2() {
        QueRequests.deleteQue()
        val patientServiceRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.004ГМУ_СП"),
                Helpers.createServiceRequestResource("A09.20.003ГМУ_СП"),
                Helpers.createServiceRequestResource("A04.16.001")
        )
        val patientBundle = Helpers.bundle("1001", "RED", patientServiceRequests)
        val responseBundle = QueRequests.createPatient(Helpers.bundle("1001", "RED", patientServiceRequests))
        val responseBundle2 = QueRequests.createPatient(Helpers.bundle("1002", "RED", patientServiceRequests))
        val responseBundle3 = QueRequests.createPatient(Helpers.bundle("1003", "RED", patientServiceRequests))
        val responseBundle4 = QueRequests.createPatient(Helpers.bundle("1004", "RED", patientServiceRequests))
        val responseBundle5 = QueRequests.createPatient(Helpers.bundle("1005", "RED", patientServiceRequests))
        val responseBundle6 = QueRequests.createPatient(Helpers.bundle("1006", "RED", patientServiceRequests))
        val responseBundle7 = QueRequests.createPatient(Helpers.bundle("1007", "RED", patientServiceRequests))
        val responseBundle8 = QueRequests.createPatient(Helpers.bundle("1008", "RED", patientServiceRequests))
        val responseBundle9 = QueRequests.createPatient(Helpers.bundle("1009", "RED", patientServiceRequests))
        val responseBundle10 = QueRequests.createPatient(Helpers.bundle("1010", "RED", patientServiceRequests))
        val responseBundle11 = QueRequests.createPatient(Helpers.bundle("1011", "RED", patientServiceRequests))
        val responseBundle12 = QueRequests.createPatient(Helpers.bundle("1012", "RED", patientServiceRequests))
        val responseBundle13 = QueRequests.createPatient(Helpers.bundle("1013", "RED", patientServiceRequests))
    }

}