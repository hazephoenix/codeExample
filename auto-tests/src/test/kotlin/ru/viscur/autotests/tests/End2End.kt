package ru.viscur.autotests.tests

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
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
    fun patientServiceRequestAdding() {
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

        //добавление ответственным дополнительного Service Request
        val additionalServiceRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП", patientId)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //в CarePlan должны быть добавленные ServiceRequests и осмотр ответственного, пациент должен встать в очередь в другой кабинет
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "B03.016.002ГМУ_СП", locationId = office101)
        ))
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    @Order(1)
    fun patientObservationFullCicle() {
        //создание пациента с 4 разными по приоритету обследованиями
        val observation101Office = "B03.016.004ГМУ_СП"
        val observation117Office = "A04.16.001"
        val observation104Office = "A09.28.029ГМУ_СП"
        val observation139Office = "СтХир"

        val patientServiceRequests = listOf(
                Helpers.createServiceRequestResource(observation101Office),
                Helpers.createServiceRequestResource(observation104Office),
                Helpers.createServiceRequestResource(observation117Office)
        )
        val patientBundle = Helpers.bundle("1001", "RED", patientServiceRequests)
        val responseBundle = QueRequests.createPatient(patientBundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        QueRequests.officeIsReady(referenceToLocation(office101))
        QueRequests.officeIsReady(referenceToLocation(office104))
        QueRequests.officeIsReady(referenceToLocation(office117))
        QueRequests.officeIsReady(referenceToLocation(office139))

        assertEquals(4, responseBundle.entry.size, "wrong number of service request")
        //прохождение всех обследований поочередно
        //office101
        val servRequestOf101office = QueRequests.patientEntered(Helpers.createListResource(patientId = patientId, officeId = office101)).first() as ServiceRequest
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val obs = Helpers.createObservation(
                code = observation101Office,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequestOf101office.id,
                valueString = "результат анализа"
        )
        QueRequests.createObservation(obs)
        QueRequests.patientLeft(referenceToLocation(office101))
        //office104
        val servRequestOf104office = QueRequests.patientEntered(Helpers.createListResource(patientId = patientId, officeId = office104)).first() as ServiceRequest
        checkQueueItems(listOf(
                QueueItemsOfOffice(office104, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val obs2 = Helpers.createObservation(
                code = observation104Office,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequestOf104office.id,
                valueString = "результат анализа"
        )
        QueRequests.createObservation(obs2)
        QueRequests.patientLeft(referenceToLocation(office104))
        //office117
        val servRequestOf117office = QueRequests.patientEntered(Helpers.createListResource(patientId = patientId, officeId = office117)).first() as ServiceRequest
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val obs3 = Helpers.createObservation(
                code = observation104Office,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequestOf117office.id,
                valueString = "результат анализа"
        )
        QueRequests.createObservation(obs3)
        QueRequests.patientLeft(referenceToLocation(office117))
        //проверка что все обследования, кроме ответственного пройдены
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation101Office, locationId = office101, status = ServiceRequestStatus.completed),
                ServiceRequestInfo(code = observation117Office, locationId = office117, status = ServiceRequestStatus.completed),
                ServiceRequestInfo(code = observation104Office, locationId = office104, status = ServiceRequestStatus.completed),
                ServiceRequestInfo(code = observation139Office, locationId = office139)
        ))
        //office 139 осмотр ответственного и завершение маршрутного листа с госпитализацией
        val servRequestOf139office = QueRequests.patientEntered(Helpers.createListResource(patientId = patientId, officeId = office139)).first() as ServiceRequest
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val obsOfRespPract = Helpers.createObservation(code = observation139Office,
                valueString = "состояние удовлетворительное",
                practitionerId =servRequestOf139office.performer?.first()?.id!!,
                basedOnServiceRequestId = servRequestOf139office.id,
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
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)
        //проверка, что маршрутный лист пациента завершен и он удален из системы очередь
        Assertions.assertEquals(ClinicalImpressionStatus.completed, completedClinicalImpression.status, "wrong status of ClinicalImpression")
        checkQueueItems(listOf())
        checkServiceRequestsOfPatient(patientId, listOf())
        checkObservationsOfPatient(patientId, listOf())
    }

}