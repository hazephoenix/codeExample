package ru.viscur.autotests.tests

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import ru.viscur.autotests.dto.*
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.observation1Office101
import ru.viscur.autotests.utils.Constants.Companion.observation1Office116
import ru.viscur.autotests.utils.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.utils.Constants.Companion.office101Id
import ru.viscur.autotests.utils.Constants.Companion.office116Id
import ru.viscur.autotests.utils.Constants.Companion.office139Id
import ru.viscur.autotests.utils.Constants.Companion.redZoneId
import ru.viscur.autotests.utils.*
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createDiagnosticReportResource
import ru.viscur.autotests.utils.Helpers.Companion.createEncounter
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.Helpers.Companion.surgeonId
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*

@Disabled("Debug purposes only")
class End2End {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun patientServiceRequestAddingCycle() {

        val bundle = bundle("7879", "RED")
        QueRequests.officeIsBusy(referenceToLocation(office139Id))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        val actServiceRequests = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServiceRequests)
        QueRequests.invitePatientToOffice(createListResource(patientId, office139Id))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office139Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //пациент вошел в кабинет
        QueRequests.officeIsReady(referenceToLocation(office139Id))
        val patientEnteredListResource = createListResource(patientId, office139Id)
        QueRequests.patientEntered(patientEnteredListResource)

        //добавление ответственным дополнительного Service Request
        val additionalServiceRequests = listOf(
                createServiceRequestResource(observation1Office101, patientId)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //проверка, что в CarePlan добавлен ServiceRequest, пациент должен снова встать в очередь в другой кабинет
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId),
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id)
        ))
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    fun patientClaimCompletingFullCycle() {
        //создание пациента с 3 разными по приоритету обследованиями
        val patientServiceRequests = listOf(
                createServiceRequestResource(observation1Office101),
                createServiceRequestResource(observation1Office116)
        )
        val patientBundle = bundle("1333", "RED", patientServiceRequests)
        val responseBundle = QueRequests.createPatient(patientBundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        QueRequests.officeIsReady(referenceToLocation(office116Id))
        QueRequests.officeIsReady(referenceToLocation(office139Id))

        assertEquals(3, responseBundle.entry.size, "wrong number of service request")

        //прохождение всех обследований поочередно
        //office101
        val servRequestOf101office = QueRequests.patientEntered(createListResource(patientId, office101Id)).
                find{it.code.code() == observation1Office101} as ServiceRequest
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val obs = createObservation(
                code = observation1Office101,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequestOf101office.id,
                valueString = "результат анализа"
        )
        QueRequests.createObservation(obs)
        QueRequests.patientLeft(createListResource(patientId, office101Id))

        //office116
        val servRequestOf116office = QueRequests.patientEntered(createListResource(patientId, office116Id)).
                find{it.code.code() == observation1Office116} as ServiceRequest
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
        val obs3 = createObservation(
                code = observation1Office116,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequestOf116office.id,
                valueString = "результат анализа"
        )
        QueRequests.createObservation(obs3)
        QueRequests.patientLeft(createListResource(patientId, office116Id))

        //проверка что все обследования, кроме ответственного пройдены
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id, status = ServiceRequestStatus.completed),
                ServiceRequestInfo(code = observation1Office116, locationId = office116Id, status = ServiceRequestStatus.completed),
                ServiceRequestInfo(code = observationOfSurgeon,  locationId = redZoneId)
        ))

        //office 139 осмотр ответственного и завершение маршрутного листа с госпитализацией
        QueRequests.invitePatientToOffice(createListResource(patientId, office139Id))
        val servRequestOfResp = QueRequests.patientEntered(createListResource(patientId, office139Id)).
                find{it.code.code() == observationOfSurgeon} as ServiceRequest
        val obsOfRespPract = createObservation(code = observationOfSurgeon,
                valueString = "состояние удовлетворительное",
                practitionerId =servRequestOfResp.performer?.first()?.id!!,
                basedOnServiceRequestId = servRequestOfResp.id,
                status = ObservationStatus.final,
                patientId = patientId
        )
        val diagnosticReportOfResp = createDiagnosticReportResource(
                diagnosisCode = "A16",
                practitionerId = surgeonId,
                status = DiagnosticReportStatus.final,
                patientId = patientId
        )
        val encounter = createEncounter(hospitalizationStr = "Клиники СибГму",patientId = patientId)
        val bundleForExamination = Bundle(entry = listOf(
                BundleEntry(obsOfRespPract),
                BundleEntry(diagnosticReportOfResp),
                BundleEntry(encounter)
        ))
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)

        //проверка, что маршрутный лист пациента завершен и он удален из системы очередь
        assertEquals(ClinicalImpressionStatus.completed, completedClinicalImpression.status, "wrong status of ClinicalImpression")
        checkQueueItems(listOf())
        checkServiceRequestsOfPatient(patientId, listOf())
        checkObservationsOfPatient(patientId, listOf())
    }
}