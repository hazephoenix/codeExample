package ru.viscur.autotests.tests

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.*
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.*

/**
 * Created at 06.11.2019 16:37 by SherbakovaMA
 *
 * Проверка сохранения продолжительности выполнения услуги
 */
@Disabled("Debug purposes only")
class ServiceRequestExecDurationTest {

    @Test
    fun test() {

        QueRequests.deleteQue()
        val observationOfSurgeonCode = "B01.057.001"
        val observationOfBloodCode = "A09.05.010"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode),
                Helpers.createServiceRequestResource(observationOfBloodCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val office139Id = "Office:139"
        val office101Id = "Office:101"

        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office139Id))

        //регистрация пациента
        val responseBundle = QueRequests.createPatient(bundle)

        val serviceRequestsFromResponse = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequestsFromResponse)

        //кабинет READY
        QueRequests.officeIsReady(referenceToLocation(office101Id))

        //пациент вошел в кабинет
        val patientEnteredListResource = Helpers.createListResource(patientId, office101Id)
        var actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)

        //началась услуга
        val bloodServiceRequestId = serviceRequestsFromResponse.find { it.code.code() == observationOfBloodCode }!!.id
        QueRequests.startObservation(bloodServiceRequestId)

        //время выполнения 3 сек
        Thread.sleep(3000)

        //услуга выполнена
        QueRequests.createObservation(Helpers.createObservation(code = "ignored", valueInt = 20, basedOnServiceRequestId = bloodServiceRequestId))

        //проверка, что услуга сохранилась длительностью 3 сек
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = office139Id),
                ServiceRequestInfo(code = observationOfBloodCode, locationId = office101Id, status = ServiceRequestStatus.waiting_result, execDuration = 3)
        ))
    }
}