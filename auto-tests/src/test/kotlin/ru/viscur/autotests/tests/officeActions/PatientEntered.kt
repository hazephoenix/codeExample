package ru.viscur.autotests.tests.officeActions

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.tests.Constants.Companion.office101Id
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
    fun goingToObservationPatientEntered() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1112", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка, что в пациент идёт на обследование
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))

        //проверка, что пациент вошёл
        QueRequests.patientEntered(Helpers.createListResource(patientId1, office101Id))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun InQueuePatientEntered() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1111", "YELLOW", servRequests)
        val bundle2 = Helpers.bundle("1112", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка, что в кабинет есть очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что ничего не произошло, нельзя войти в кабинет со статуса IN_QUEUE
        QueRequests.patientEntered(Helpers.createListResource(patientId1, office101Id))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}