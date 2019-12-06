package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_202
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_202_ID
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class QueueReport {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
        QueRequests.cancelAllActivePatient()
    }

    @Test
    fun gettingQueueReport () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_202_ID))
        val servRequests = listOf(
                createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(OBSERVATION1_OFFICE_202)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка наличия очереди в разные кабинеты
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(OFFICE_202_ID, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //получение отчета о полном состоянии очереди
        val queueItems = QueRequests.getQueueReport()
        val queueItemOffice101 = queueItems.find{it.officeId== OFFICE_101_ID}!!
        val queueItemOffice119 = queueItems.find{it.officeId== OFFICE_202_ID}!!

        //проверка отчета по состоянию очереди
        assertEquals(2, queueItems.size, "wrong office number in report")
        assertEquals(1,  queueItemOffice101.queueSize, "wrong patient number for $OFFICE_101_ID")
        assertEquals(1,  queueItemOffice119.queueSize, "wrong patient number for $OFFICE_202_ID")
    }

    @Test
    fun gettingOfficeQueueReport () {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_202_ID))
        val servRequests = listOf(
                createServiceRequestResource(OBSERVATION1_OFFICE_101)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(OBSERVATION1_OFFICE_202)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests2)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка наличия очереди в разные кабинеты
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(OFFICE_202_ID, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //получение отчета о состоянии очереди в 101
        val queueItemOffice101 = QueRequests.getOfficeQueueReport(OFFICE_101_ID)

        //проверка отчета по состоянию очереди в 101
        assertEquals(OFFICE_101_ID, queueItemOffice101.officeId, "wrong office id in report")
        assertEquals(1,  queueItemOffice101.queueSize, "wrong patient number for $OFFICE_101_ID")
    }
}