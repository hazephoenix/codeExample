package ru.viscur.autotests.tests.officeActions

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class NextPatient {

    companion object {
        val office101 = "Office:101"
        val observation1Office101 = "B03.016.002ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun inviteNextWithInQueue() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "RED", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка, что в офис есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что первый из очереди идёт в офис на обследование
        QueRequests.inviteNextPatientToOffice(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun inviteNextWithGoingAndOnObservation() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "RED", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val bundle3 = Helpers.bundle("1113", "YELLOW", servRequests)

        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        QueRequests.patientEntered(Helpers.createListResource(patientId1, office101))
        QueRequests.inviteNextPatientToOffice(referenceToLocation(office101))

        //проверка, что в офис есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                        ))
        ))

        //проверка, что первый из очереди идёт в офис на обследование
        QueRequests.inviteNextPatientToOffice(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }

    @Test
    fun inviteNextWithoutNextInQueue() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //проверка, что пациент идет на обследование
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //проверка, что ничего не изменилось, т.к. следующего в очереди не существует
        QueRequests.inviteNextPatientToOffice(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }

}