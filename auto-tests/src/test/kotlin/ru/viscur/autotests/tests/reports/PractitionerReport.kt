package ru.viscur.autotests.tests.  reports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.observation1Office101
import ru.viscur.autotests.utils.Constants.Companion.observation1Office116
import ru.viscur.autotests.utils.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.utils.Constants.Companion.office101Id
import ru.viscur.autotests.utils.Constants.Companion.office116Id
import ru.viscur.autotests.utils.Constants.Companion.practitioner1Office101
import ru.viscur.autotests.utils.Constants.Companion.practitioner1Office116
import ru.viscur.autotests.utils.Constants.Companion.practitioner2Office101
import ru.viscur.autotests.utils.Constants.Companion.surgeon1Id
import ru.viscur.autotests.utils.Constants.Companion.surgeon2Id
import ru.viscur.autotests.utils.Constants.Companion.urologist1Id
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.bundleForSurgeon2
import ru.viscur.autotests.utils.Helpers.Companion.bundleForUrologist
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class PractitionerReport {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
        QueRequests.cancelAllActivePatient()
    }

    @Test
    fun gettingAllPractitionerQueueReport () {
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

        //привязка practitioners к кабинетам
        QueRequests.setPractitionerActivityAndLocation(practitioner1Office101,true, office101Id)
        QueRequests.setPractitionerActivityAndLocation(practitioner2Office101, true, office101Id)
        QueRequests.setPractitionerActivityAndLocation(practitioner1Office116, true, office116Id)

        //получение отчета о полном состоянии очередей в кабинеты
        val queueItemsForPractitioners = QueRequests.getAllPractitionersWorkload()
        val patientsForPractitioner1Office101 = queueItemsForPractitioners.find {it.practitioner!!.practitionerId == practitioner1Office101}!!.items
        val patientsForPractitioner2Office101 = queueItemsForPractitioners.find {it.practitioner!!.practitionerId == practitioner2Office101}!!.items
        val patientsForPractitioner1Office116 = queueItemsForPractitioners.find {it.practitioner!!.practitionerId == practitioner1Office116}!!.items

        //проверка, что отчет содержит очередь для каждого специалиста
        assertEquals(3, queueItemsForPractitioners.size, "wrong office number in report for practitioner")
        assertEquals(patientId1, patientsForPractitioner1Office101.first().patientId, "wrong patient for $practitioner1Office101")
        assertEquals(patientId1, patientsForPractitioner2Office101.first().patientId, "wrong patient for $practitioner2Office101")
        assertEquals(patientId2, patientsForPractitioner1Office116.first().patientId, "wrong patient for $practitioner1Office101")
    }

    @Test
    fun gettingDiagnosticPractitionerByIdQueueReport() {
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
        QueRequests.createPatient(bundle2)


        //привязка practitioner к кабинету
        QueRequests.setPractitionerActivityAndLocation(practitioner1Office101,true, office101Id)

        //получение отчета о нагрузке на конкретного practitioner
        val queueItems = QueRequests.getPractitionersWorkloadById(practitioner1Office101)

        //проверка отчета по состоянию очереди для practitioner
        assertEquals(1, queueItems.items.size, "wrong number of patients for $practitioner1Office101")
        assertEquals(patientId1, queueItems.items.first().patientId,  "wrong patient in queue for practitioner $practitioner1Office101")
        assertEquals(office101Id, queueItems.officeId,  "wrong office for practitioner $practitioner1Office101")
    }

    @Test
    fun gettingRespPractitionerReportByIdWithOnInspectionAndOnResponsability() {
        //создание пациентов на отвественности
        val bundle1 = bundle("1120", "YELLOW")
        val bundle2 = bundleForUrologist("1121", "RED")
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val additionalServiceRequests = listOf(
                createServiceRequestResource(observationOfSurgeon, patientId2)
        )
        val bundleForExaminination = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )

        //добавление Service Request для осмотра хирурга
        QueRequests.addServiceRequests(bundleForExaminination)

        //получение данных о пациентах на ответственности у хирургов и уролога
        val patientsOfSurgeon1 = QueRequests.getPractitionersWorkloadById(surgeon1Id)
        val patientsOfSurgeon2 = QueRequests.getPractitionersWorkloadById(surgeon2Id)
        val patientsOfUrologist1 = QueRequests.getPractitionersWorkloadById(urologist1Id)

        //проверка пациентов на ответственности у хирургов и уролога, у surgeon1Id есть patientId1 на ответственности и patientId2 для осмотра, у 2 хирурга только patientId2 на осмотр
        //у уролога есть patientId2 на ответственности, но он не отображается в очереди к нему, пока не пройдет назначенный осмотр у хирурга
        assertEquals(2, patientsOfSurgeon1.items.size, "wrong patient number for $surgeon1Id")
        assertNotNull(patientsOfSurgeon1.items.find {it.patientId == patientId1} )
        assertNotNull(patientsOfSurgeon1.items.find {it.patientId == patientId2} )
        assertEquals(1, patientsOfSurgeon2.items.size, "wrong patient number for $surgeon1Id")
        assertNotNull(patientsOfSurgeon2.items.find {it.patientId == patientId2} )
        assertEquals(0, patientsOfUrologist1.items.size, "wrong patient number for $surgeon1Id")
    }

    @Test
    fun gettingPatientsOfResponsablePractitioner() {
        //создание пациентов на ответственности surgeon2
        val bundle1 = bundleForSurgeon2("1120", "YELLOW")
        val bundle2 = bundleForSurgeon2("1121", "YELLOW")
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val surgeon2Patients =  QueRequests.getPatientsOfResponsable(surgeon2Id).patients

        //проверка, что у surgeon2 2 пациента на ответственности
        assertEquals(2, surgeon2Patients.size,"wrong patient number for practitioner: $surgeon2Id")
    }
}