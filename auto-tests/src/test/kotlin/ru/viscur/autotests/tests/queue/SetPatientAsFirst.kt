package ru.viscur.autotests.tests.queue

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class SetPatientAsFirst {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun settingPatientAsFirstOnlyInQueue () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val bundle3 = Helpers.bundle("1113", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        //проверка, что в кабинет есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что зеленый пациент поставлен первым в очереди
        QueRequests.setPatientFirst(Helpers.createListResource(patientId1, OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun settingPatientAsFirstWithGoingToObservationAndOnObservation() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val bundle3 = Helpers.bundle("1113", "RED", servRequests)
        val bundle4 = Helpers.bundle("1114", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        val patientId4 = patientIdFromServiceRequests(QueRequests.createPatient(bundle4).resources(ResourceType.ServiceRequest))
        QueRequests.inviteNextPatientToOffice(referenceToLocation(OFFICE_101_ID))
        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))

        //проверка, что зеленый пациент поставлен первым в очередь после ON_OBSERVATION, GOING_TO_OBSERVATION
        QueRequests.setPatientFirst(Helpers.createListResource(patientId4, OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}