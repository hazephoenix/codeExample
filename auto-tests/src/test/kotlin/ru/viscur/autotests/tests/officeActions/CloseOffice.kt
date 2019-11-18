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
class CloseOffice {

    companion object {
        val office101 = "Office:101"
        val observation1Office101 = "B03.016.002ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }
    @Test
    fun closingOfficeGoingToObservation() {
        val observation101Office = "B03.016.002ГМУ_СП"
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation101Office)
        )
        val bundleRed1 = Helpers.bundle("1111", "RED", servRequests)
        val bundleRed2 = Helpers.bundle("1112", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))

        //проверка, что пациент на статусе GOING_TO_OBSERVATION
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что кабинет не закрыть со статуса WAITING_PATIENT
        QueRequests.officeIsClosed(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun closingOfficeInQueue() {
        val observation101Office = "B03.016.002ГМУ_СП"
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation101Office)
        )
        val bundleRed1 = Helpers.bundle("1111", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))

        //проверка, что в кабинет стоит пациент
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //проверка, что кабинет закрыт, очередь расформирована
        QueRequests.officeIsClosed(referenceToLocation(office101))
        checkQueueItems(listOf())
    }

    @Test
    fun closingOfficeOnObservation() {
        val observation101Office = "B03.016.002ГМУ_СП"
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation101Office)
        )
        val bundleRed1 = Helpers.bundle("1111", "RED", servRequests)
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        QueRequests.patientEntered(Helpers.createListResource(patientId, office101))
        QueRequests.officeIsClosed(referenceToLocation(office101))

        //проверка, что в кабинет стоит пациент
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))

        //проверка, что кабинет не закрыть со статуса ON_OBSERVATION
        QueRequests.officeIsClosed(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                ))
        ))
    }
}