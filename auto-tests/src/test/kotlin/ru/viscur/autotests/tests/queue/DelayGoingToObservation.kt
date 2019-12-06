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
class DelayGoingToObservation {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun delayPatientWithInQueue() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)
        val bundleRed3 = Helpers.bundle("1113", "RED", servReq1)
        val bundleRed4 = Helpers.bundle("1114", "RED", servReq1)
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed3).resources(ResourceType.ServiceRequest))
        val patientId4 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed4).resources(ResourceType.ServiceRequest))
        QueRequests.inviteNextPatientToOffice(referenceToLocation(OFFICE_101_ID))
        QueRequests.patientEntered(Helpers.createListResource(patientId2, OFFICE_101_ID))

        //очередь до delay
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //перестановка пациента в очереди
        QueRequests.delayPatient(referenceToPatient(patientId1))

        //проверка состояния очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun delayPatientWithNoQueueAfter() {
        //создание очереди
        val servReq1 = listOf(Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))

        //проверка, что пациент идет на обследование
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //проверка, что ничего не произошло, т.к. за пациентом никого в очереди
        QueRequests.delayPatient(referenceToPatient(patientId1))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }
}