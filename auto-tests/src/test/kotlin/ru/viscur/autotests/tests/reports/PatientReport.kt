package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.observation1Office149
import ru.viscur.autotests.utils.Constants.Companion.observation1Office202
import ru.viscur.autotests.utils.Constants.Companion.office149Id
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class PatientReport {

    @Test
    fun patientObservationHistory() {
        //регистрация пациента и создание истории обследований
        val servRequests = listOf(
                createServiceRequestResource(observation1Office202)
        )
        val bundle = bundle("9999", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val serviceRequestsFromResponse = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequestsFromResponse)
        val xrayServiceRequestId = serviceRequestsFromResponse.find{it.code.code() == observation1Office202}!!.id

        //создание обследования с продолжительностью 2 секунды
        QueRequests.startObservation(xrayServiceRequestId)
        Thread.sleep(5000)
        QueRequests.createObservation(createObservation(code = "ignored", valueInt = 20, basedOnServiceRequestId = xrayServiceRequestId))
        val observationsOfPatient = QueRequests.getPatientObservationHistory(patientId)

        //проверка, что обледование попало в историю обследований пациента за последние сутки
        assertNotNull(observationsOfPatient.find{it.code == observation1Office202}?.code, "no observation in history")
    }

    @Test
    fun patientQueueHistory() {
        //регистрация пациента и создание истории очереди
        val expectedStatusRdy = "READY"
        val expectedStatusInQueue = "IN_QUEUE"

        QueRequests.deleteQue()
        QueRequests.officeIsBusy(referenceToLocation(office149Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office149)
        )
        val bundle = bundle("9998", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        QueRequests.officeIsReady(referenceToLocation(office149Id))
        val patientId = patientIdFromServiceRequests(responseBundle)

        //получение истории очереди пациента за последние сутки
        val patientQueueHistory = QueRequests.getPatientQueueHistory(patientId)

        //проверка, что в истории пациента есть записи о продолжительности нахождения в очереди по статусам
        assertNotNull(patientQueueHistory.find{it.status == expectedStatusRdy}!!.duration, "patient status ready duration is null")
        assertNotNull(patientQueueHistory.find{it.status == expectedStatusInQueue}!!.duration, "patient status inqueue duration is null")
        assertNotNull(patientQueueHistory.find{it.officeId == office149Id}!!.duration, "patient queue for $office149Id duration is null")
    }
}