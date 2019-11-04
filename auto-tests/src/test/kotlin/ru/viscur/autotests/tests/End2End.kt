package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation

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
        val servRequests = listOf(
                Helpers.createServiceRequestResource("СтХир")
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val office139Id = "Office:139"

        QueRequests.officeIsBusy(referenceToLocation(office139Id))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        //проверка что есть первый Service Request и он в нужный кабинет
        val actServiceRequest = responseBundle.entry.get(0).resource as ServiceRequest
        assertNotNull(actServiceRequest.subject?.id, "wrong id patient")
        assertEquals(office139Id, actServiceRequest.locationReference?.first()?.id, "code ... ")

        val patientId = actServiceRequest.subject?.id!!
        assertEquals(servRequests.first().code.code(), actServiceRequest.code.code(), "code ... ")

        var actPatient = QueRequests.resource(ResourceType.Patient, patientId)

        checkQueueItems(listOf(
                QueueItemsOfOffice(office139Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
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
        actPatient = QueRequests.resource(ResourceType.Patient, patientId)

        //проверка что в кабинете необходимые обследования и пациент
        assertEquals(1, actServicesInOffice.size, "wrong number of office's service requests")
        assertEquals(PatientQueueStatus.ON_OBSERVATION, actPatient.extension.queueStatus, "wrong patient status")

        //т к единственное обследование - осмотр отв. - завершение обследования:
        val actServiceInOffice = actServicesInOffice.first()
        assertEquals(servRequests.first().code.code(), actServiceInOffice.code.code(), "wrong service request code in office")
        assertEquals(patientId, actServiceInOffice.subject?.id, "wrong patientId of service request")

        //завершение обращения
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
        QueRequests.officeIsBusy(referenceToLocation(office139))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        val serviceRequest = responseBundle.entry.first().resource as ServiceRequest
        val patientId = serviceRequest.subject?.id!!
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
    }

}