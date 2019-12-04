package ru.viscur.autotests.tests.queue

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_139_ID
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class ForceInvite {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun inQueuePatientForceInviteToOffice() {
        //создание очереди
        val servReq1 = Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        val bundle = Helpers.bundle("1122", "RED", listOf(servReq1))

        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest))

        //проверка, что пациент стоит в очереди в 101
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        QueRequests.officeIsBusy(referenceToLocation(OFFICE_139_ID))

        //проверка, что пациента вызвали в 139
        QueRequests.invitePatientToOffice(Helpers.createListResource(patientId, OFFICE_139_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_139_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }

    @Test
    fun notInQueuePatientForceInviteToOffice() {
        //создание очереди
        val servReq1 = Helpers.createServiceRequestResource(OBSERVATION1_OFFICE_101)
        val bundle = Helpers.bundle("1122", "RED", listOf(servReq1))

        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest))
        QueRequests.deletePatientFromQueue(referenceToPatient(patientId))

        //проверка, что пациента нет в очереди
        checkQueueItems(listOf())

        QueRequests.officeIsBusy(referenceToLocation(OFFICE_139_ID))

        //проверка, что пациента вызвали в 139
        QueRequests.invitePatientToOffice(Helpers.createListResource(patientId, OFFICE_139_ID))
        checkQueueItems(listOf(
                QueueItemsOfOffice(OFFICE_139_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }
}