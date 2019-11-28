package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.tests.Constants.Companion.observation1Office116
import ru.viscur.autotests.tests.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.tests.Constants.Companion.office101Id
import ru.viscur.autotests.tests.Constants.Companion.office116Id
import ru.viscur.autotests.tests.Constants.Companion.office140Id
import ru.viscur.autotests.tests.Constants.Companion.pratitioner101Office
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.bundleForUrologist
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.Helpers.Companion.surgeonId
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class PractitionerReport {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
        QueRequests.cancelAllActivePatient()
    }

    @Test
    fun gettingAllPractitionerWorkload () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(observation1Office116)
        )

        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //получение отчета о полном состоянии нагрузки на practitioners
        val queueItemForPractitioners = QueRequests.getAllPractitionersWorkload()
        val queueItemForOffice101 = queueItemForPractitioners.find{it.officeId== office101Id}!!
        val queueItemForOffice116 = queueItemForPractitioners.find{it.officeId== office116Id}!!

        //проверка отчета по состоянию нагрузки на practitioners
        assertEquals(3, queueItemForPractitioners.size, "wrong office number in report for practitioner")
        assertEquals(1,  queueItemForOffice101.queueSize, "wrong patient number for $office101Id")
        assertEquals(1,  queueItemForOffice116.queueSize, "wrong patient number for $office116Id")
    }

    @Test
    fun gettingPractitionerWorkloadById () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(observation1Office116)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //получение отчета о нагрузке на конкретного practitioner
        val queueItems = QueRequests.getPractitionersWorkloadById(pratitioner101Office)
        val queueItemForPractitioner101Office = queueItems.first()

        //проверка отчета по состоянию очереди для practitioner
        assertEquals(1, queueItems.size, "wrong office number in report")
        assertEquals(office101Id, queueItemForPractitioner101Office.officeId, "wrong office id in report")
        assertEquals(1,  queueItemForPractitioner101Office.queueSize, "wrong patient number for $office101Id")
    }

   /* @Test
    fun gettingPractitionerWorkloadByPeriod () {
        val servRequests = listOf(
                createServiceRequestResource(observation150Office)
        )
        val bundle1 = bundle("1120", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        QueRequests.officeIsReady(referenceToLocation("Office:150"))
        QueRequests.patientEntered(createListResource(patientId1, "Office:150"))
    }*/

    @Test
    fun gettingPatientsOfResponsablePractitioner() {
        //создание пациентов
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "YELLOW", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val surgeonPatients =  QueRequests.getPatientsOfResponsable(surgeonId).patients

        //проверка, что у хирурга 2 пациента на ответственности
        assertEquals(2, surgeonPatients.size,"wrong patient number for practitioner: $surgeonId")
    }

    @Test
    fun gettingPatientOfPractitionerRespAndIspection() {

        val bundle1 = bundle("1120", "YELLOW")
        val bundle2 = bundleForUrologist("1121", "RED")

        val patient1ServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId1 = patientIdFromServiceRequests(patient1ServRequests)

        val patient2ServRequests = QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest)
        val patientId2 = patientIdFromServiceRequests(patient2ServRequests)

        QueRequests.invitePatientToOffice(createListResource(patientId2, office140Id))
        QueRequests.patientEntered(createListResource(patientId2, office140Id))
        val additionalServiceRequests = listOf(
                createServiceRequestResource(observationOfSurgeon, patientId2)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        val additionalserv = QueRequests.addServiceRequests(bundleForExamin)
    }

}