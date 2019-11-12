package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
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
        val observationCode = "B03.016.002ГМУ_СП"
        val observationCode2 = "A04.16.001"
        val observationCode3 = "A09.20.003ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun responseShouldReturnServiceRequests () {
        val diagnosis = " {\"diagnosis\": \"A01\",\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"],\"gender\": \"male\"}"
        QueRequests.getSupposedServRequests(diagnosis).
                assertThat().body("entry.size()", equalTo(6))
    }

    @Test
    fun serviceRequestAdding() {
        val observationOfSurgeonCode = "СтХир"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139)
        ))

        val additionalServiceRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП", patientId)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //в CarePlan должен быть добавлен новый ServiceRequest
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "B03.016.002ГМУ_СП", locationId = office101, status = ServiceRequestStatus.active)
        ))
    }

    @Test
    fun gettingRightServiceRequestsInOffice() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode),
                Helpers.createServiceRequestResource(observationCode2),
                Helpers.createServiceRequestResource(observationCode3)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)

        //создание пациента
        QueRequests.officeIsReady(referenceToLocation(Observations.office101))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val servRequestsInOffice = QueRequests.patientEntered(Helpers.createListResource(patientId, Observations.office101))

        assertEquals(2, servRequestsInOffice.size, "wrong number of service requests in $office101")
        assertEquals(observationCode, servRequestsInOffice.get(0).code.code(), "wrong service request in office")
        assertEquals(observationCode3, servRequestsInOffice.get(1).code.code(), "wrong service request in office")
    }

    @Test
    fun activeServiceRequestCancelling () {
        val observationOfSurgeonCode = "СтХир"
        val observation = "B03.016.002ГМУ_СП"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observation)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val serviceRequestId = serviceRequests.get(0).id
        val patientId = patientIdFromServiceRequests(serviceRequests)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "B03.016.002ГМУ_СП", locationId = office101)
        ))

        /*QueRequests.cancelServiceRequest(serviceRequestId)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139)
        ))*/
    }

    @Test
    fun WaitingResultServiceRequestCancelling () {
        val observationOfSurgeonCode = "СтХир"
        val observationBloodCode= "B03.016.002ГМУ_СП"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observationBloodCode)
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
        /*QueRequests.createObservation(observation)
        QueRequests.cancelServiceRequest(serviceRequest.id)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "B03.016.002ГМУ_СП", locationId = office101, status = ServiceRequestStatus.cancelled)
        ))*/
    }
}