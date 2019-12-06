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
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class CancelEntering {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun cancelFirstPatientOnObservationEntering() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)

        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))

        //пациент вошел в кабинет
        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //отмена входа пациента, пациент должен вернуться первым в очередь
        QueRequests.cancelEntering(referenceToPatient(patientId1))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun cancelSecondPatientOnObservationEntering() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)
        val bundleRed3 = Helpers.bundle("1113", "RED", servReq1)

        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed3).resources(ResourceType.ServiceRequest))

        //пациент вошел в кабинет
        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))
        QueRequests.inviteNextPatientToOffice(referenceToLocation(OFFICE_101_ID))
        QueRequests.patientEntered(Helpers.createListResource(patientId2, OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //отмена входа пациента, пациент должен вернуться первым в очередь
        QueRequests.cancelEntering(referenceToPatient(patientId2))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun cancelFirstPatientGoingToObservationEntering() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)

        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))

        //пациент идет на обследование
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
            ))
        ))
        //отмена входа пациента, пациент должен вернуться первым в очередь
        QueRequests.cancelEntering(referenceToPatient(patientId1))
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
            ))
        ))
    }

    @Test
    fun cancelSecondPatientGoingToObservationEntering() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)
        val bundleRed3 = Helpers.bundle("1113", "RED", servReq1)

        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed3).resources(ResourceType.ServiceRequest))

        //второй пациент идет на обследование
        QueRequests.inviteNextPatientToOffice(referenceToLocation(OFFICE_101_ID))
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemInfo(patientId2, PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
            ))
        ))

        //отмена входа пациента, пациент должен вернуться первым в очередь
        QueRequests.cancelEntering(referenceToPatient(patientId2))
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
            ))
        ))
    }

    @Test
    fun cancelWrongStatusPatientEntering() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        QueRequests.deletePatientFromQueue(referenceToPatient(patientId2))

        //проверка состояния очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проврека, чтоничего не происходит, т.к. нельзя отменить вход пациента со статусов IN_QUEUE, READY
        QueRequests.cancelEntering(referenceToPatient(patientId1))
        QueRequests.cancelEntering(referenceToPatient(patientId2))

        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}