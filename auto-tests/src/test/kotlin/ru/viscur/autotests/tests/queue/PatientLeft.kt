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
class PatientLeft {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun firstOnObservationPatientLeft() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))

        //проверка, что в кабинете пациент на обследовании
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что пациент вышел из кабинета и его снова поставило в очередь в этот же кабинет, т.к. не пройдено обследование
        QueRequests.patientLeft(Helpers.createListResource(patientId1, OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun secondOnObservationPatientLeft() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
            Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))
        QueRequests.inviteNextPatientToOffice(referenceToLocation(OFFICE_101_ID))
        QueRequests.patientEntered(Helpers.createListResource(patientId2, OFFICE_101_ID))

        //проверка, что в кабинете пациенты на обследовании
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION)
            ))
        ))

        //проверка, что пациент второй пациент вышел из кабинета и его снова поставило в очередь в этот же кабинет, т.к. не пройдено обследование
        QueRequests.patientLeft(Helpers.createListResource(patientId2, OFFICE_101_ID))
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
            ))
        ))
    }

    @Test
    fun wrongStatusPatientLeft() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val bundle3 = Helpers.bundle("1113", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        QueRequests.deletePatientFromQueue(referenceToPatient(patientId3))

        //проверка, что в кабинет есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что ничего не происходит, нельзя выйти из кабинета со статусов IN_QUEUE, GOING_TO_OBSERVATION, READY
        QueRequests.patientLeft(Helpers.createListResource(patientId1, OFFICE_101_ID))
        QueRequests.patientLeft(Helpers.createListResource(patientId2, OFFICE_101_ID))
        QueRequests.patientLeft(Helpers.createListResource(patientId3, OFFICE_101_ID))

        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}