package ru.viscur.autotests.tests.serviceRequests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ObservationInfo
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_116
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION2_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION_OF_SURGEON
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.RED_ZONE_ID
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.checkObservationsOfPatient
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class Observations {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingObservation() {
        //создание пациента
        val servRequests = listOf(
                createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом registered
        val obs = createObservation(
                code = OBSERVATION1_OFFICE_101,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId,
                valueString = "good quality of blood"
        )
        QueRequests.createObservation(obs)

        //проверка созданного Observation
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = OBSERVATION1_OFFICE_101,
                        status = ObservationStatus.registered,
                        valueStr = "good quality of blood"
                )
        ))

        //проверка изменения статуса в Service Requests пациента
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = OBSERVATION1_OFFICE_101,
                        locationId = OFFICE_101_ID,
                        status = ServiceRequestStatus.waiting_result
                ),
                ServiceRequestInfo(
                        code = OBSERVATION_OF_SURGEON,
                        locationId = RED_ZONE_ID,
                        status = ServiceRequestStatus.active
                )
        ))
    }

    @Test
    fun updatingRegisteredObservation() {
        //создание пациента
        val servRequests = listOf(
                createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом registered
        val obs = createObservation(
                code = OBSERVATION1_OFFICE_101,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId
        )
        val actObs = QueRequests.createObservation(obs)

        //обновление Observation - статус final, заполнено значение valueString
        val updatedObs = createObservation(
                code = OBSERVATION1_OFFICE_101,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequstId,
                id = actObs.id,
                valueString = "quality of blood is good")
        QueRequests.updateObservation(updatedObs)

        //проверка обновленного Observation
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = OBSERVATION1_OFFICE_101,
                        status = ObservationStatus.final,
                        valueStr = "quality of blood is good")))

        //проверка изменения статуса в Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = OBSERVATION1_OFFICE_101,
                        locationId = OFFICE_101_ID,
                        status = ServiceRequestStatus.completed
                ),
                ServiceRequestInfo(
                        code = OBSERVATION_OF_SURGEON,
                        locationId = RED_ZONE_ID,
                        status = ServiceRequestStatus.active
                )
        ))
    }

    @Test
    fun updatingFinalObservation() {
        //создание пациента
        val servRequests = listOf(
            createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом final
        val obs = createObservation(
            code = OBSERVATION1_OFFICE_101,
            status = ObservationStatus.final,
            basedOnServiceRequestId = servRequstId,
            valueString = "quality of blood is good"
        )
        val actObs = QueRequests.createObservation(obs)

        //обновление Observation - статус final, изменено значение valueString
        val updatedObs = createObservation(
            code = OBSERVATION1_OFFICE_101,
            status = ObservationStatus.final,
            basedOnServiceRequestId = servRequstId,
            id = actObs.id,
            valueString = "quality of blood is bad")
        QueRequests.updateObservation(updatedObs)

        //проверка обновленного Observation
        checkObservationsOfPatient(patientId, listOf(
            ObservationInfo(
                basedOnId = servRequstId,
                code = OBSERVATION1_OFFICE_101,
                status = ObservationStatus.final,
                valueStr = "quality of blood is bad")))
    }

    @Test
    fun gettingPatientObservations() {
        //создание пациента
        val servRequests = listOf(
            createServiceRequestResource(OBSERVATION1_OFFICE_101),
            createServiceRequestResource(OBSERVATION2_OFFICE_101),
            createServiceRequestResource(OBSERVATION1_OFFICE_116)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequest1Office101Id = actServRequests.find { it.code.code() == OBSERVATION1_OFFICE_101 }!!.id
        val servRequest2Office101Id = actServRequests.find { it.code.code() == OBSERVATION2_OFFICE_101 }!!.id

        //создание observation со статусами registered и final
        val finalObservationOffice101 =
            createObservation(
                basedOnServiceRequestId = servRequest1Office101Id,
                status = ObservationStatus.final,
                valueString = "здоров"
            )
        val registeredObservationOffice101 =
            createObservation(
                basedOnServiceRequestId = servRequest2Office101Id,
                status = ObservationStatus.registered
            )
        QueRequests.createObservation(finalObservationOffice101)
        QueRequests.createObservation(registeredObservationOffice101)

        //получение данных об обследованиях пациента
        val registeredObservations = QueRequests.getPatientObservationsByStatus(patientId, ObservationStatus.registered)
        val finalObservations = QueRequests.getPatientObservationsByStatus(patientId, ObservationStatus.final)
        val allObservations = QueRequests.observations(patientId)

        //проверка, что возвращаются соответственные обследования
        assertEquals(1, registeredObservations.size, "wrong number of registered observations for $patientId")
        assertEquals(1, finalObservations.size, "wrong number of final observations for $patientId")
        assertEquals(2, allObservations.size, "wrong number of observations for patient $patientId")
        assertEquals(OBSERVATION1_OFFICE_101, finalObservations.first().code.code(), "wrong final observation code for service request: $servRequest1Office101Id")
        assertEquals(OBSERVATION2_OFFICE_101, registeredObservations.first().code.code(), "wrong registered observation code for service request: $servRequest2Office101Id")
    }
}