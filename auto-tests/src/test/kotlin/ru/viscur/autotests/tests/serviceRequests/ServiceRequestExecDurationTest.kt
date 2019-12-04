package ru.viscur.autotests.tests.serviceRequests

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION_OF_SURGEON
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.RED_ZONE_ID
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
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)

        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        QueRequests.officeIsBusy(referenceToLocation(RED_ZONE_ID))

        //регистрация пациента
        val responseBundle = QueRequests.createPatient(bundle)

        val serviceRequestsFromResponse = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequestsFromResponse)

        //кабинет READY
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))

        //пациент вошел в кабинет
        val patientEnteredListResource = Helpers.createListResource(patientId, OFFICE_101_ID)
        var actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)

        //началась услуга
        val bloodServiceRequestId = serviceRequestsFromResponse.find { it.code.code() == OBSERVATION1_OFFICE_101 }!!.id
        QueRequests.startObservation(bloodServiceRequestId)

        //время выполнения 3 сек
        Thread.sleep(3000)

        //услуга выполнена
        QueRequests.createObservation(Helpers.createObservation(code = "ignored", valueInt = 20, basedOnServiceRequestId = bloodServiceRequestId))

        //проверка, что услуга сохранилась длительностью 3 сек
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID),
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID, status = ServiceRequestStatus.waiting_result, execDuration = 3)
        ))
    }
}