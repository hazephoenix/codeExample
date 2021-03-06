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
class CloseOffice {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun closingOfficeGoingToObservation() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundleRed1 = Helpers.bundle("1111", "RED", servRequests)
        val bundleRed2 = Helpers.bundle("1112", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))

        //проверка, что пациент на статусе GOING_TO_OBSERVATION
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что кабинет не закрыть со статуса WAITING_PATIENT
        QueRequests.officeIsClosed(referenceToLocation(OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun closingOfficeInQueue() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundleRed1 = Helpers.bundle("1111", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))

        //проверка, что в кабинет стоит пациент
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что кабинет закрыт, очередь расформирована
        QueRequests.officeIsClosed(referenceToLocation(OFFICE_101_ID))
        checkQueueItems(listOf())
    }

    @Test
    fun closingOfficeOnObservation() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundleRed1 = Helpers.bundle("1111", "RED", servRequests)
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        QueRequests.patientEntered(Helpers.createListResource(patientId, OFFICE_101_ID))
        QueRequests.officeIsClosed(referenceToLocation(OFFICE_101_ID))

        //проверка, что в кабинет стоит пациент
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))

        //проверка, что кабинет не закрыть со статуса ON_OBSERVATION
        QueRequests.officeIsClosed(referenceToLocation(OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
    }
}