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
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class DelayGoingToObservation {

    companion object {
        val office101 = "Office:101"
        val observation1Office101 = "B03.016.002ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun delayPatientWithInQueue() {
        val servReq1 = listOf(Helpers.createServiceRequestResource(observation1Office101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)
        val bundleRed2 = Helpers.bundle("1112", "RED", servReq1)
        val bundleRed3 = Helpers.bundle("1113", "RED", servReq1)
        val bundleRed4 = Helpers.bundle("1114", "RED", servReq1)
        QueRequests.officeIsReady(referenceToLocation(office101))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed3).resources(ResourceType.ServiceRequest))
        val patientId4 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed4).resources(ResourceType.ServiceRequest))

        QueRequests.inviteNextPatientToOffice(referenceToLocation(office101))
        QueRequests.patientEntered(Helpers.createListResource(patientId2, office101))

        //очередь до delay
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что пациент был переставлен в очереди
        QueRequests.delayPatient(referenceToPatient(patientId1))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun delayPatientWithNoQueueAfter() {
        val servReq1 = listOf(Helpers.createServiceRequestResource(observation1Office101))
        val bundleRed1 = Helpers.bundle("1111", "RED", servReq1)

        QueRequests.officeIsReady(referenceToLocation(office101))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))

        //проверка, что пациент идет на обследование
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //проверка, что ничего не произошло, т.к. за пациентом никого в очереди
        QueRequests.delayPatient(referenceToPatient(patientId1))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }

}