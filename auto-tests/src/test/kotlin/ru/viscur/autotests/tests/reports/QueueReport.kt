package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.tests.Constants.Companion.observation1Office202
import ru.viscur.autotests.tests.Constants.Companion.office101Id
import ru.viscur.autotests.tests.Constants.Companion.office202Id
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class QueueReport {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
        QueRequests.cancelAllActivePatient()
    }

    @Test
    fun gettingQueueReport () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office202Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(observation1Office202)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка наличия очереди в разные кабинеты
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office202Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //получение отчета о полном состоянии очереди
        val queueItems = QueRequests.getQueueReport()
        val queueItemOffice101 = queueItems.find{it.officeId== office101Id}!!
        val queueItemOffice119 = queueItems.find{it.officeId== office202Id}!!

        //проверка отчета по состоянию очереди
        assertEquals(2, queueItems.size, "wrong office number in report")
        assertEquals(1,  queueItemOffice101.queueSize, "wrong patient number for $office101Id")
        assertEquals(1,  queueItemOffice119.queueSize, "wrong patient number for $office202Id")
    }

    @Test
    fun gettingOfficeQueueReport () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office202Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(observation1Office202)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка наличия очереди в разные кабинеты
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office202Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //получение отчета о состоянии очереди в 101
        val queueItemOffice101 = QueRequests.getOfficeQueueReport(office101Id)

        //проверка отчета по состоянию очереди в 101
        assertEquals(office101Id, queueItemOffice101.officeId, "wrong office id in report")
        assertEquals(1,  queueItemOffice101.queueSize, "wrong patient number for $office101Id")
    }
}