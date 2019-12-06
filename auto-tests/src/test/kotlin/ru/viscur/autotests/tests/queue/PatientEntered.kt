package ru.viscur.autotests.tests.queue

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_116
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_116_ID
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class PatientEntered {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun firstGoingToObservationPatientEntered() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка, что в пациент идёт на обследование
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))

        //проверка, что пациент вошёл
        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun secondGoingToObservationPatientEntered() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        val servRequests = listOf(
            Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        QueRequests.inviteNextPatientToOffice(referenceToLocation(OFFICE_101_ID))
        //проверка, что в пациенты идут на обследование
        QueueItemsOfOffice(OFFICE_101_ID, listOf(
            QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
            QueueItemInfo(patientId2, PatientQueueStatus.GOING_TO_OBSERVATION)
        ))

        //проверка, что пациент вошёл
        QueRequests.patientEntered(Helpers.createListResource(patientId2, OFFICE_101_ID))
        checkQueueItems(listOf(
            QueueItemsOfOffice(OFFICE_101_ID, listOf(
                QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                QueueItemInfo(patientId2, PatientQueueStatus.ON_OBSERVATION)
            ))
        ))
    }

    @Test
    fun wrongStatusPatientEntered() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        QueRequests.officeIsReady(referenceToLocation(OFFICE_116_ID))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_116)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val bundle3 = Helpers.bundle("1113", "RED", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))

        //проверка, что в кабинеты есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(OFFICE_116_ID, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //проверка, что ничего не произошло, нельзя войти в кабинет со статусов IN_QUEUE, ON_OBSERVATION и из другой очереди
        QueRequests.patientEntered(Helpers.createListResource(patientId1, OFFICE_101_ID))
        QueRequests.patientEntered(Helpers.createListResource(patientId2, OFFICE_101_ID))
        QueRequests.patientEntered(Helpers.createListResource(patientId2, OFFICE_101_ID))

        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
            )),
                QueueItemsOfOffice(OFFICE_116_ID, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.GOING_TO_OBSERVATION)
            ))
        ))
    }
}