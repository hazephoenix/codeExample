package ru.viscur.autotests.tests.serviceRequests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_116
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION2_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION_OF_SURGEON
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION_URINE_OFFICE_104
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_104_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_116_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_140_ID
import ru.viscur.autotests.utils.Constants.Companion.RED_ZONE_ID
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
    fun serviceRequestAdding() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION_OF_SURGEON)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID)
        ))

        //создание дополнительного Service Request
        val additionalServiceRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101, patientId)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //проверка, что в в CarePlan добавлен новый ServiceRequest
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.active)
        ))
    }

    @Test
    fun activeServiceRequestCancellingById () {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION_OF_SURGEON),
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101),
                Helpers.createServiceRequestResource(OBSERVATION2_OFFICE_101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val serviceRequestId = serviceRequests.find {it.code.code()== OBSERVATION1_OFFICE_101}!!.id
        val patientId = patientIdFromServiceRequests(serviceRequests)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = OBSERVATION2_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.active)

        ))

        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelServiceRequest(serviceRequestId)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = OBSERVATION2_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.active)
        ))
    }

    @Test
    fun WaitingResultServiceRequestCancellingById () {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION_OF_SURGEON),
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
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
                ServiceRequestInfo(OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID),
                ServiceRequestInfo(OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.cancelled)
        ))
    }

    @Test
    fun ServiceRequestByOfficeCancelling () {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101),
                Helpers.createServiceRequestResource(OBSERVATION2_OFFICE_101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequests)

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID),
                ServiceRequestInfo(code = OBSERVATION2_OFFICE_101, locationId = OFFICE_101_ID),
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID)
        ))

        //отмена и проверка что отменные Service Request перешли статус cancelled
        QueRequests.cancelOfficeServiceRequests(patientId, OFFICE_101_ID)
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = OBSERVATION2_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID)
        ))
    }

    @Test
    fun ServiceRequestWrongOfficeCancelling () {
        //создание пациента
        val servRequests = listOf(
            Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101),
            Helpers.createServiceRequestResource(OBSERVATION2_OFFICE_101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequests)

        checkServiceRequestsOfPatient(patientId, listOf(
            ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID),
            ServiceRequestInfo(code = OBSERVATION2_OFFICE_101, locationId = OFFICE_101_ID),
            ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID)
        ))

        //отмена сервис реквестов в неверный офис и проверка что ничего не происходит
        QueRequests.cancelOfficeServiceRequests(patientId, OFFICE_116_ID)
        checkServiceRequestsOfPatient(patientId, listOf(
            ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.active),
            ServiceRequestInfo(code = OBSERVATION2_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.active),
            ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID, status = ServiceRequestStatus.active)
        ))
    }

    @Test
    fun gettingRightServiceRequestsInOffice() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101),
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_116),
                Helpers.createServiceRequestResource(OBSERVATION2_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //вход в кабинет
        val servRequestsInOffice = QueRequests.patientEntered(createListResource(patientId, OFFICE_101_ID))

        //проверка, что в кабинете соответствующие Service Requests
        assertEquals(2, servRequestsInOffice.size, "wrong number of service requests in $OFFICE_101_ID")
        assertEquals(OBSERVATION1_OFFICE_101, servRequestsInOffice.find{it.code.code() == OBSERVATION1_OFFICE_101 }?.code!!.code() , "wrong service request in office")
        assertEquals(OBSERVATION2_OFFICE_101, servRequestsInOffice.find{it.code.code() == OBSERVATION2_OFFICE_101 }?.code!!.code(), "wrong service request in office")
    }

    @Test
    fun gettingAllServiceRequestsWhenEnteredWrongOffice() {
        //создание пациента
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101),
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_116),
                Helpers.createServiceRequestResource(OBSERVATION2_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //пациент вошел в офис, в котором у него не назначены обследования
        QueRequests.invitePatientToOffice(createListResource(patientId, OFFICE_140_ID))
        val allActServRequest = QueRequests.patientEntered(createListResource(patientId, OFFICE_140_ID))

        //проверка, что в офисе, в котором не назначены обследования получаем список всех Service Request пациента
        assertEquals(4, allActServRequest.size, "wrong service requests for patient $patientId")
    }

    @Test
    fun urineServiceRequestIgnoring() {
        //создание пациента с обследованиям мочи и приёмом ответственного
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION_OF_SURGEON),
                Helpers.createServiceRequestResource(OBSERVATION_URINE_OFFICE_104)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        QueRequests.officeIsBusy(referenceToLocation(RED_ZONE_ID))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest))

        //проверка, что анализ мочи есть в Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_URINE_OFFICE_104, locationId = OFFICE_104_ID, status = ServiceRequestStatus.active),
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID)
        ))

        //проверка, что анализ мочи игнорируется и пациента сразу отправляет на приём к ответственному
        checkQueueItems(listOf(
                QueueItemsOfOffice(RED_ZONE_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}