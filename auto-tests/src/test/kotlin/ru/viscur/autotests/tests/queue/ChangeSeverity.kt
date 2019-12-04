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
class ChangeSeverity {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun GreenToRed() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests)
        val bundle3 = Helpers.bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        //проверка, что очередь отсортирована правильно после смены severity
        QueRequests.changeSeverity(patientId1, "RED")
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun RedToGreen() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests)
        val bundle3 = Helpers.bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        //проверка, что очередь отсортирована правильно после смены severity
        QueRequests.changeSeverity(patientId3, "GREEN")
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun YellowToYellow() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests)
        val bundle3 = Helpers.bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        //проверка, что очередь не изменилась
        QueRequests.changeSeverity(patientId1, "YELLOW")
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}