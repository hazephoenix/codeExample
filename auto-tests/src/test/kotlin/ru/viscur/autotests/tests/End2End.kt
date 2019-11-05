package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.*
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class End2End {

    companion object {
        //фельдшер
        const val paramedicId = Helpers.paramedicId
        //кто делает все observation
        const val diagnosticAssistantId = Helpers.diagnosticAssistantId
        //ответсвенный
        const val respPractitionerId = Helpers.surgeonId

        val office116 = "Office:116"
        val office117 = "Office:117"
        val office101 = "Office:101"
        val office139 = "Office:139"
        val office104 = "Office:104"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    @Order(1)
    fun patientGettingRequestsAndFinishingThem() {
        QueRequests.deleteQue()
        val observationOfSurgeonCode = "СтХир"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        QueRequests.officeIsBusy(referenceToLocation(office139))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        val serviceRequestsFromResponse = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequestsFromResponse)

        //проверка Service Request-ов из ответа
        compareServiceRequests(patientId, listOf(ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139)), serviceRequestsFromResponse)

        //проверка состояний в базе: состояние очереди, назначения, обследования
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        checkServiceRequestsOfPatient(patientId, listOf(ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139)))
        checkObservationsOfPatient(patientId, listOf())
       /* checkPatientsOfResp(listOf(
                PatientsOfRespInfo("фельдшер_Колосова", listOf(
                        PatientOfRespInfo(patientId, Severity.RED)
                ))
        ))*/

        //кабинет READY
        QueRequests.officeIsReady(referenceToLocation(office139))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //пациент вошел в кабинет
        val patientEnteredListResource = Helpers.createListResource(patientId, office139)
        val actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)

        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))

        //проверка что в кабинете необходимые обследования и пациент
        compareServiceRequests(patientId, listOf(ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139)), serviceRequestsFromResponse)

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

    @Test
    @Order(1)
    fun patientAdditionalServRequestAdding() {
        QueRequests.deleteQue()
        val servRequests = listOf(
                Helpers.createServiceRequestResource("СтХир")
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
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
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП", patientId),
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
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "B03.016.002ГМУ_СП", locationId = office101),
                ServiceRequestInfo(code = "B03.016.006ГМУ_СП", locationId = office104)
        ))
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    @Order(1)
    fun fullPositiveE2ePatient() {
        //Todo написать полный тест с поочередным прохождением всех requests, заполнением observations и завершением
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
        //Todo додумать и написать тест с добавлением множества пациентов
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