package ru.viscur.autotests.tests.reports

import org.junit.jupiter.api.Assertions
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
class PractitionerWorkloadReport {

    companion object {
        val office101 = "Office:101"
        val office116 = "Office:116"
        val observationOffice101 = "B03.016.002ГМУ_СП"
        val observationOffice116 = "A04.16.001"
        val pratitioner101Office = "фельдшер_Колосова"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun gettingAllPractitionerWorkload () {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice116)
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
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //получение отчета о полном состоянии нагрузки на practitioners
        val queueItemForPractitioners = QueRequests.getAllPractitionersWorkload()
        val queueItemForOffice101 = queueItemForPractitioners.find{it.officeId== office101}!!
        val queueItemForOffice116 = queueItemForPractitioners.find{it.officeId== office116}!!
        //проверка отчета по состоянию нагрузки на practitioners
        Assertions.assertEquals(3, queueItemForPractitioners.size, "wrong office number in report for practitioner")
        Assertions.assertEquals(1,  queueItemForOffice101.queueSize, "wrong patient number for $office101")
        Assertions.assertEquals(1,  queueItemForOffice116.queueSize, "wrong patient number for $office116")
    }

    @Test
    fun gettingPractitionerWorkloadById () {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))

        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOffice101)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice116)
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
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //получение отчета о нагрузке на конкретного practitioner
        val queueItems = QueRequests.getPractitionersWorkloadById(pratitioner101Office)
        val queueItemForPractitioner101Office = queueItems.first()!!
        //проверка отчета по состоянию очереди для practitioner
        Assertions.assertEquals(1, queueItems.size, "wrong office number in report")
        Assertions.assertEquals(office101, queueItemForPractitioner101Office.officeId, "wrong office id in report")
        Assertions.assertEquals(1,  queueItemForPractitioner101Office.queueSize, "wrong patient number for $office101")
    }
}