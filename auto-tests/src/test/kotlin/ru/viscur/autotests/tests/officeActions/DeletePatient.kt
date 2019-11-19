package ru.viscur.autotests.tests.officeActions


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class DeletePatient {

    companion object {
        val office116 = "Office:116"
        val office117 = "Office:117"
        val office101 = "Office:101"
        val office139 = "Office:139"
        val office104 = "Office:104"

        val observCode = "B03.016.002ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun deleteMiddlePositionPatientInQue() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        QueRequests.deletePatientFromQueue(referenceToPatient(patientId2))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun deleteFirstPositionPatientInQue() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        QueRequests.deletePatientFromQueue(referenceToPatient(patientId1))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun deleteLastPositionPatientInQue() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        QueRequests.deletePatientFromQueue(referenceToPatient(patientId3))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun deletePatientWithoutNextInQue() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        QueRequests.deletePatientFromQueue(referenceToPatient(patientId1))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf())
        ))
    }

    @Test
    fun deletePatientGoingToObservationWithNextInQue() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        QueRequests.deletePatientFromQueue(referenceToPatient(patientId1))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

}
