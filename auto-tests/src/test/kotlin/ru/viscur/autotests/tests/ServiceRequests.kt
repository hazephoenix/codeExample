package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class ServiceRequests {

    companion object {
        val office101 = "Office:101"
        val office104 = "Office:104"
        val office117 = "Office:117"
        val office139 = "Office:139"
        val office140 = "Office:140"
        val redZone = "Office:RedZone"
        val observationOfSurgeonCode = "СтХир"
        val observation1Office101 = "B03.016.002ГМУ_СП"
        val observation2Office101 = "A09.20.003ГМУ_СП"
        val observation1Office116 = "A04.16.001"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun gettingServiceRequestsByDiagnosis () {
        val diagnosis = " {\"diagnosis\": \"A01\",\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"],\"gender\": \"male\"}"
        QueRequests.getSupposedServRequests(diagnosis).
                assertThat().body("entry.size()", equalTo(6))
    }

    @Test
    fun serviceRequestAdding() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = redZone)
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
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = redZone, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = observation1Office101, locationId = office101, status = ServiceRequestStatus.active)
        ))
    }

    @Test
    fun activeServiceRequestCancellingById () {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val serviceRequestId = serviceRequests.get(0).id
        val patientId = patientIdFromServiceRequests(serviceRequests)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = redZone),
                ServiceRequestInfo(code = observation1Office101, locationId = office101)
        ))
        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelServiceRequest(serviceRequestId)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = redZone),
                ServiceRequestInfo(code = observation1Office101, locationId = office101, status = ServiceRequestStatus.cancelled)
        ))
    }

    @Test
    fun WaitingResultServiceRequestCancellingById () {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101))
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
                ServiceRequestInfo(observationOfSurgeonCode, locationId = redZone),
                ServiceRequestInfo(observation1Office101, locationId = office101, status = ServiceRequestStatus.cancelled)
        ))
    }

    @Test
    fun ServiceRequestByOfficeCancelling () {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequests)

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101),
                ServiceRequestInfo(code = observation2Office101, locationId = office101),
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = redZone)
        ))
        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelOfficeServiceRequests(patientId, office101)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observation2Office101, locationId = office101, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = redZone)
        ))
    }

    @Test
    fun gettingRightServiceRequestsInOffice() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation1Office116),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)

        //создание пациента
        QueRequests.officeIsReady(referenceToLocation(office101))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //вход в кабинет
        val servRequestsInOffice = QueRequests.patientEntered(Helpers.createListResource(patientId, Observations.office101))
        //проверка, что в кабинете соответствующие Service Requests
        assertEquals(2, servRequestsInOffice.size, "wrong number of service requests in $office101")
        assertEquals(observation1Office101, servRequestsInOffice.find{it.code.code() == observation1Office101}?.code!!.code() , "wrong service request in office")
        assertEquals(observation2Office101, servRequestsInOffice.find{it.code.code() == observation2Office101}?.code!!.code(), "wrong service request in office")
    }

    @Test
    fun gettingAllServiceRequestsWhenEnteredWrongOffice() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation1Office116),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)

        //создание пациента
        QueRequests.officeIsReady(referenceToLocation(Observations.office101))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        QueRequests.invitePatientToOffice(createListResource(patientId, office140))
        val allActServRequest = QueRequests.patientEntered(createListResource(patientId, office140))
        //проверка, что в офисе, в который не назначены  Service Request получаем список всех Service Request пациента
        assertEquals(4, allActServRequest.size, "wrong service requests for patient $patientId")
    }
}