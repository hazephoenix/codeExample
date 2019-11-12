package ru.viscur.autotests.tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ObservationInfo
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkObservationsOfPatient
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class Observations {

    companion object {

        val office101 = "Office:101"
        val office104 = "Office:104"
        val office139 = "Office:139"
        val observationCode = "B03.016.002ГМУ_СП"
        val observationCode2 = "СтХир"
        val observationCode3 = "A04.16.001"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingObservation() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)

        //создание пациента
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом registered
        val obs = Helpers.createObservation(
                code = observationCode,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId,
                valueString = "good quality of blood"
        )
        QueRequests.createObservation(obs)

        //проверка созданного Observation
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = observationCode,
                        status = ObservationStatus.registered,
                        valueStr = "good quality of blood"
                )
        ))

        //проверка изменения статуса в Service Requests пациента
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observationCode,
                        locationId = office101,
                        status = ServiceRequestStatus.waiting_result
                ),
                ServiceRequestInfo(
                        code = observationCode2,
                        locationId = office139,
                        status = ServiceRequestStatus.active
                )
        ))
    }

    @Test
    fun updatingObservation() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)

        //создание пациента
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом registered
        val obs = Helpers.createObservation(
                code = observationCode,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId
        )
        val actObs = QueRequests.createObservation(obs)

        //обновление Observation - статус final, заполнено значение valueString
        val updatedObs = Helpers.createObservation(
                code = observationCode,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequstId,
                id = actObs.id,
                valueString = "quality of blood is good")
        QueRequests.updateObservation(updatedObs)

        //проверка обновленного Observation
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = observationCode,
                        status = ObservationStatus.final,
                        valueStr = "quality of blood is good")))

        //проверка изменения статуса в Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(
                        code = observationCode,
                        locationId = office101,
                        status = ServiceRequestStatus.completed
                ),
                ServiceRequestInfo(
                        code = observationCode2,
                        locationId = office139,
                        status = ServiceRequestStatus.active
                )
        ))
    }
}