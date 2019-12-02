package ru.viscur.autotests.tests.serviceRequests

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.observation1Office101
import ru.viscur.autotests.utils.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.utils.Constants.Companion.office101Id
import ru.viscur.autotests.utils.Constants.Companion.redZoneId
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
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(redZoneId))

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
        val bloodServiceRequestId = serviceRequestsFromResponse.find { it.code.code() == observation1Office101 }!!.id
        QueRequests.startObservation(bloodServiceRequestId)

        //время выполнения 3 сек
        Thread.sleep(3000)

        //услуга выполнена
        QueRequests.createObservation(Helpers.createObservation(code = "ignored", valueInt = 20, basedOnServiceRequestId = bloodServiceRequestId))

        //проверка, что услуга сохранилась длительностью 3 сек
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId),
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id, status = ServiceRequestStatus.waiting_result, execDuration = 3)
        ))
    }
}