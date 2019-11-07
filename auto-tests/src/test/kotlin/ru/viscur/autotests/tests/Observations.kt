package ru.viscur.autotests.tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ObservationInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkObservationsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class Observations {

    companion object {
        val observationCode = "B03.016.002ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun addingObservation() {
        //Todo проверить изменения в carePlan
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)

        //создание пациента
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)
        val servRequstId = actServRequests.first().id

        //создание Observation со статусом final
        val obs = Helpers.createObservation(
                code = observationCode,
                status = ObservationStatus.final,
                basedOnServiceRequestId = servRequstId,
                valueString = "good quality of blood"
        )
        QueRequests.createObservation(obs)
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = observationCode,
                        status = ObservationStatus.final,
                        valueStr = "good quality of blood"
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
        checkObservationsOfPatient(patientId, listOf(
                ObservationInfo(
                        basedOnId = servRequstId,
                        code = observationCode,
                        status = ObservationStatus.final,
                        valueStr = "quality of blood is good")))
    }

}