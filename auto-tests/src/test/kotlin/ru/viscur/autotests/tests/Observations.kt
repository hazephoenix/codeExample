package ru.viscur.autotests.tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ObservationInfo
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.tests.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.tests.Constants.Companion.office101Id
import ru.viscur.autotests.tests.Constants.Companion.redZoneId
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.checkObservationsOfPatient
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class Observations {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingObservation() {
        //создание пациента
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом registered
        val obs = createObservation(
                code = observation1Office101,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId,
                valueString = "good quality of blood"
        )
        QueRequests.createObservation(obs)

        //проверка созданного Observation
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = observation1Office101,
                        status = ObservationStatus.registered,
                        valueStr = "good quality of blood"
                )
        ))

        //проверка изменения статуса в Service Requests пациента
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observation1Office101,
                        locationId = office101Id,
                        status = ServiceRequestStatus.waiting_result
                ),
                ServiceRequestInfo(
                        code = observationOfSurgeon,
                        locationId = redZoneId,
                        status = ServiceRequestStatus.active
                )
        ))
    }

    @Test
    fun updatingObservation() {
        //создание пациента
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом registered
        val obs = createObservation(
                code = observation1Office101,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId
        )
        val actObs = QueRequests.createObservation(obs)

        //обновление Observation - статус final, заполнено значение valueString
        val updatedObs = createObservation(
                code = observation1Office101,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequstId,
                id = actObs.id,
                valueString = "quality of blood is good")
        QueRequests.updateObservation(updatedObs)

        //проверка обновленного Observation
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = observation1Office101,
                        status = ObservationStatus.final,
                        valueStr = "quality of blood is good")))

        //проверка изменения статуса в Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observation1Office101,
                        locationId = office101Id,
                        status = ServiceRequestStatus.completed
                ),
                ServiceRequestInfo(
                        code = observationOfSurgeon,
                        locationId = redZoneId,
                        status = ServiceRequestStatus.active
                )
        ))
    }
}