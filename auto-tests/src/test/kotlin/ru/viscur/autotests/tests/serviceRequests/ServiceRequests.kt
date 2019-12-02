package ru.viscur.autotests.tests.serviceRequests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.tests.Constants.Companion.observation1Office116
import ru.viscur.autotests.tests.Constants.Companion.observation2Office101
import ru.viscur.autotests.tests.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.tests.Constants.Companion.observationUrineOffice104
import ru.viscur.autotests.tests.Constants.Companion.office101Id
import ru.viscur.autotests.tests.Constants.Companion.office104Id
import ru.viscur.autotests.tests.Constants.Companion.office140Id
import ru.viscur.autotests.tests.Constants.Companion.redZoneId
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class ServiceRequests {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun gettingServiceRequestsByDiagnosis () {
        //создание диагноза
        val diagnosisCode = "A16"
        val diagnosis = mapOf(
                "diagnosis" to diagnosisCode,
                "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
                "gender" to "male"
        )

        //получение предположительных Service Request по диагнозу
        val servRequestsList = QueRequests.getSupposedServRequests(diagnosis)

        //проверка количества предположительных Service Requests
        assertEquals(18, servRequestsList.size, "wrong number of service requests for diagnosis: $diagnosisCode")
    }

    @Test
    fun serviceRequestAdding() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeon)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId)
        ))

        //создание дополнительного Service Request
        val additionalServiceRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101, patientId)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //проверка, что в в CarePlan добавлен новый ServiceRequest
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id, status = ServiceRequestStatus.active)
        ))
    }

    @Test
    fun activeServiceRequestCancellingById () {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeon),
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val serviceRequestId = serviceRequests.get(0).id
        val patientId = patientIdFromServiceRequests(serviceRequests)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId),
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id)
        ))

        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelServiceRequest(serviceRequestId)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId),
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id, status = ServiceRequestStatus.cancelled)
        ))
    }

    @Test
    fun WaitingResultServiceRequestCancellingById () {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeon),
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val serviceRequest = serviceRequests.get(0)
        val patientId = patientIdFromServiceRequests(serviceRequests)
        val observation= Helpers.createObservation(
                code = serviceRequest.code.code(),
                basedOnServiceRequestId = serviceRequest.id,
                status = ObservationStatus.registered
        )
        QueRequests.createObservation(observation)

        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelServiceRequest(serviceRequest.id)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(observationOfSurgeon, locationId = redZoneId),
                ServiceRequestInfo(observation1Office101, locationId = office101Id, status = ServiceRequestStatus.cancelled)
        ))
    }

    @Test
    fun ServiceRequestByOfficeCancelling () {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequests)

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id),
                ServiceRequestInfo(code = observation2Office101, locationId = office101Id),
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId)
        ))

        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelOfficeServiceRequests(patientId, office101Id)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observation2Office101, locationId = office101Id, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId)
        ))
    }

    @Test
    fun gettingRightServiceRequestsInOffice() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation1Office116),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //вход в кабинет
        val servRequestsInOffice = QueRequests.patientEntered(Helpers.createListResource(patientId, office101Id))

        //проверка, что в кабинете соответствующие Service Requests
        assertEquals(2, servRequestsInOffice.size, "wrong number of service requests in $office101Id")
        assertEquals(observation1Office101, servRequestsInOffice.find{it.code.code() == observation1Office101 }?.code!!.code() , "wrong service request in office")
        assertEquals(observation2Office101, servRequestsInOffice.find{it.code.code() == observation2Office101 }?.code!!.code(), "wrong service request in office")
    }

    @Test
    fun gettingAllServiceRequestsWhenEnteredWrongOffice() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation1Office116),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //пациент вошел в офис, в котором у него не назначены обследования
        QueRequests.invitePatientToOffice(createListResource(patientId, office140Id))
        val allActServRequest = QueRequests.patientEntered(createListResource(patientId, office140Id))

        //проверка, что в офисе, в котором не назначены обследования получаем список всех Service Request пациента
        assertEquals(4, allActServRequest.size, "wrong service requests for patient $patientId")
    }

    @Test
    fun urineServiceRequestIgnoring() {
        //создание пациента с обследованиям мочи и приёмом ответственного
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeon),
                Helpers.createServiceRequestResource(observationUrineOffice104)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        QueRequests.officeIsBusy(referenceToLocation(redZoneId))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest))

        //проверка, что анализ мочи есть в Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationUrineOffice104, locationId = office104Id, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId)
        ))

        //проверка, что анализ мочи игнорируется и пациента сразу отправляет на приём к ответственному
        checkQueueItems(listOf(
                QueueItemsOfOffice(redZoneId, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}