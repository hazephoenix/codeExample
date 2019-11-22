package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions
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
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class QueueReport {

    companion object {
        val office101 = "Office:101"
        val office202 = "Office:202"
        val observationOffice101 = "B03.016.002"
        val observationOffice202 = "A06.04.004"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun gettingQueueReport () {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office202))

        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice202)
        )

        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка наличия очереди в разные кабинеты
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office202, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //получение отчета о полном состоянии очереди
        val queueItems = QueRequests.getQueueReport()
        val queueItemOffice101 = queueItems.find{it.officeId== office101}!!
        val queueItemOffice119 = queueItems.find{it.officeId== office202}!!
        //проверка отчета по состоянию очереди
        Assertions.assertEquals(2, queueItems.size, "wrong office number in report")
        Assertions.assertEquals(1,  queueItemOffice101.queueSize, "wrong patient number for $office101")
        Assertions.assertEquals(1,  queueItemOffice119.queueSize, "wrong patient number for $office202")
    }

    @Test
    fun gettingOfficeQueueReport () {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office202))

        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice202)
        )

        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val bundle2 = Helpers.bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка наличия очереди в разные кабинеты
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office202, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //получение отчета о состоянии очереди в 101
        val queueItems = QueRequests.getOfficeQueueReport(office101)
        val queueItemOffice101 = queueItems.first()
        //проверка отчета по состоянию очереди в 101
        Assertions.assertEquals(1, queueItems.size, "wrong office number in report")
        Assertions.assertEquals(office101, queueItemOffice101.officeId, "wrong office id in report")
        Assertions.assertEquals(1,  queueItemOffice101.queueSize, "wrong patient number for $office101")
    }
}