package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class PatientReport {

    companion object {
        val observationXray = "A06.30.004.001"
        val observationOffice149 = "A03.09.001"
        val office149 = "Office:149"

    }

    @Test
    fun patientObservationHistory() {
        //регистрация пациента и создание истории обследований
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationXray)
        )
        val bundle = Helpers.bundle("9999", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val serviceRequestsFromResponse = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequestsFromResponse)
        val xrayServiceRequestId = serviceRequestsFromResponse.find{it.code.code() == observationXray}!!.id

        //создание обследования с продолжительностью 2 секунды
        QueRequests.startObservation(xrayServiceRequestId)
        Thread.sleep(2000)
        QueRequests.createObservation(Helpers.createObservation(code = "ignored", valueInt = 20, basedOnServiceRequestId = xrayServiceRequestId))
        val observationsOfPatient = QueRequests.getPatientObservationHistory(patientId)

        //проверка, что обледование попало в историю обследований пациента за последние сутки
        assertEquals(observationXray, observationsOfPatient.find{it.duration==2}?.code, "wrong observation in history")
    }

    @Test
    fun patientQueueHistory() {
        //регистрация пациента и создание истории очереди
        val expectedQueueStatus = "IN_QUEUE"
        QueRequests.deleteQue()
        QueRequests.officeIsBusy(referenceToLocation(office149))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice149)
        )
        val bundle = Helpers.bundle("9998", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        QueRequests.officeIsReady(referenceToLocation(office149))
        val patientId = patientIdFromServiceRequests(responseBundle)

        //получение истории очереди
        val patientQueueHistory = QueRequests.getPatientQueueHistory(patientId)

        //проверка, что в истории пациента за последние сутки есть запись о том, что он стоял в 149
        assertEquals(expectedQueueStatus, patientQueueHistory.find{it.officeId == office149}!!.status, "wrong status of patient in queue history")
    }
}