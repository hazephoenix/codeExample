package ru.viscur.autotests.tests.officeActions

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class PatientLeft {

    companion object {
        val office101 = "Office:101"
        val observation1Office101 = "B03.016.002ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun onObservationPatientLeft() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        QueRequests.patientEntered(Helpers.createListResource(patientId1, office101))

        //проверка, что в кабинете пациент на обследовании
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что пациент вышел из кабинет и его снова поставило в очередь в этот же кабинет, т.к. не пройдено обследование
        QueRequests.patientLeft(Helpers.createListResource(patientId1, office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun inQueuePatientLeft() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка, что в кабинет есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что нельзя выйти из кабинета, не войдя в него перед выходом
        QueRequests.patientLeft(Helpers.createListResource(patientId1, office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

}