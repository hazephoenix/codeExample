package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class PractitionerReport {

    companion object {
        val office101 = "Office:101"
        val office116 = "Office:116"
        val observationOffice101 = "B03.016.002"
        val observationOffice116 = "A04.16.001"
        val pratitioner101Office = "фельдшер_Колосова"
        val observation150Office = "A03.18.001"
        val surgeonId = Helpers.surgeonId
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
        QueRequests.cancelAllActivePatient()
    }

    @Test
    fun gettingAllPractitionerWorkload () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice116)
        )

        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //получение отчета о полном состоянии нагрузки на practitioners
        val queueItemForPractitioners = QueRequests.getAllPractitionersWorkload()
        val queueItemForOffice101 = queueItemForPractitioners.find{it.officeId== office101}!!
        val queueItemForOffice116 = queueItemForPractitioners.find{it.officeId== office116}!!

        //проверка отчета по состоянию нагрузки на practitioners
        assertEquals(3, queueItemForPractitioners.size, "wrong office number in report for practitioner")
        assertEquals(1,  queueItemForOffice101.queueSize, "wrong patient number for $office101")
        assertEquals(1,  queueItemForOffice116.queueSize, "wrong patient number for $office116")
    }

    @Test
    fun gettingPractitionerWorkloadById () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice116)
        )
        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //получение отчета о нагрузке на конкретного practitioner
        val queueItems = QueRequests.getPractitionersWorkloadById(pratitioner101Office)
        val queueItemForPractitioner101Office = queueItems.first()

        //проверка отчета по состоянию очереди для practitioner
        assertEquals(1, queueItems.size, "wrong office number in report")
        assertEquals(office101, queueItemForPractitioner101Office.officeId, "wrong office id in report")
        assertEquals(1,  queueItemForPractitioner101Office.queueSize, "wrong patient number for $office101")
    }

   /* @Test
    fun gettingPractitionerWorkloadByPeriod () {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation150Office)
        )
        val bundle1 = Helpers.bundle("1120", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        QueRequests.officeIsReady(referenceToLocation("Office:150"))
        QueRequests.patientEntered(Helpers.createListResource(patientId1, "Office:150"))
    }*/

    @Test
    fun gettingPatientsOfResponsablePractitioner() {
        //создание пациентов
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val bundle1 = Helpers.bundle("1120", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val surgeonPatients =  QueRequests.getPatientsOfResponsable("хирург_Иванов").patients

        //проверка, что у хирурга 2 пациента на ответственности
        assertEquals(2, surgeonPatients.size,"wrong patient number for practitioner: $surgeonId")
    }
}