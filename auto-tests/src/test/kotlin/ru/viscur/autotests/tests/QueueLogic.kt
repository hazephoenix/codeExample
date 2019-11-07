package ru.viscur.autotests.tests


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class QueueLogic {

    companion object {
        //фельдшер
        val paramedicId = Helpers.paramedicId
        //кто делает все observation
        val diagnosticAssistantId = Helpers.diagnosticAssistantId
        //ответсвенный
        val respPractitionerId = Helpers.surgeonId

        val office116 = "Office:116"
        val office117 = "Office:117"
        val office101 = "Office:101"
        val office139 = "Office:139"
        val office104 = "Office:104"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun patientsShouldBeInDifferentQueues() {
        //Patient1
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle1 = bundle("1111", "RED", listOf(servReq1))
        //Patient2
        val servReq2 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle2 = bundle("1112", "RED", listOf(servReq2))

        //добавление 2 пациентов на узи
        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))
        val servReqUzi1 = QueRequests.createPatient(bundle1).entry.first().resource as ServiceRequest
        val patientId1 = servReqUzi1.subject!!.id!!
        val servReqUzi2 = QueRequests.createPatient(bundle2).entry.first().resource as ServiceRequest
        val patientId2 = servReqUzi2.subject!!.id!!

        //проверка что оба пациента в очереди в разные кабинеты узи
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun patientForceInviteToOffice() {
        val servReq1 = Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        val bundle = bundle("1122", "RED", listOf(servReq1))

        QueRequests.officeIsReady(referenceToLocation(office101))
        val actServReq = QueRequests.createPatient(bundle).entry.first().resource as ServiceRequest
        val patientId = actServReq.subject?.id!!

        //проверка, что пациент стоит в очереди в 101
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        QueRequests.officeIsBusy(referenceToLocation(office139))
        QueRequests.invitePatientToOffice(createListResource(patientId, office139))

        //проверка, что пациента вызвали в 139
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }

    @Test
    fun GreenYellowRedSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1122", "RED", servRequests)
        val responseBundle1 = QueRequests.createPatient(bundle1).entry.get(0).resource as ServiceRequest
        val responseBundle2 = QueRequests.createPatient(bundle2).entry.get(0).resource as ServiceRequest
        val responseBundle3 = QueRequests.createPatient(bundle3).entry.get(0).resource as ServiceRequest

        //проверка корректного формарования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(responseBundle3.subject?.id!!, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(responseBundle2.subject?.id!!, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(responseBundle1.subject?.id!!, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun deleteMiddlePositionPatientInQue() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "GREEN", servRequests)
        val responseBundle1 = QueRequests.createPatient(bundle1)
        val responseBundle2 = QueRequests.createPatient(bundle2)
        val responseBundle3 = QueRequests.createPatient(bundle3)
        val patientId1 = patientIdFromServiceRequests(responseBundle1.resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(responseBundle2.resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(responseBundle3.resources(ResourceType.ServiceRequest))

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
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "GREEN", servRequests)
        val responseBundle1 = QueRequests.createPatient(bundle1)
        val responseBundle2 = QueRequests.createPatient(bundle2)
        val responseBundle3 = QueRequests.createPatient(bundle3)
        val patientId1 = patientIdFromServiceRequests(responseBundle1.resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(responseBundle2.resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(responseBundle3.resources(ResourceType.ServiceRequest))

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
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "GREEN", servRequests)
        val responseBundle1 = QueRequests.createPatient(bundle1)
        val responseBundle2 = QueRequests.createPatient(bundle2)
        val responseBundle3 = QueRequests.createPatient(bundle3)
        val patientId1 = patientIdFromServiceRequests(responseBundle1.resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(responseBundle2.resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(responseBundle3.resources(ResourceType.ServiceRequest))

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
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val responseBundle1 = QueRequests.createPatient(bundle1)
        val patientId1 = patientIdFromServiceRequests(responseBundle1.resources(ResourceType.ServiceRequest))

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
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val responseBundle1 = QueRequests.createPatient(bundle1)
        val responseBundle2 = QueRequests.createPatient(bundle2)
        val patientId1 = patientIdFromServiceRequests(responseBundle1.resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(responseBundle2.resources(ResourceType.ServiceRequest))

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

    @Test
    fun patientsShouldBeInDifferentQueues2() {
        //Todo додумать тест
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundleRed1 = bundle("1111", "RED", listOf(servReq1))
        val bundleRed2 = bundle("1112", "RED", listOf(servReq1))
        val bundleRed3 = bundle("1113", "RED", listOf(servReq1))

        val bundleYellow1 = bundle("1114", "YELLOW", listOf(servReq1))
        val bundleYellow2 = bundle("1115", "YELLOW", listOf(servReq1))
        val bundleYellow3 = bundle("1116", "YELLOW", listOf(servReq1))

        val bundleGreen1 = bundle("1117", "GREEN", listOf(servReq1))
        val bundleGreen2 = bundle("1118", "GREEN", listOf(servReq1))
        val bundleGreen3 = bundle("1119", "GREEN", listOf(servReq1))

        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))

        val servReqUzi1 = QueRequests.createPatient(bundleRed1).entry.first().resource as ServiceRequest
        val patientId1 = servReqUzi1.subject!!.id!!
        val servReqUzi2 = QueRequests.createPatient(bundleRed2).entry.first().resource as ServiceRequest
        val patientId2 = servReqUzi2.subject!!.id!!
        val servReqUzi3 = QueRequests.createPatient(bundleYellow1).entry.first().resource as ServiceRequest
        val patientId3 = servReqUzi3.subject!!.id!!
        val servReqUzi4 = QueRequests.createPatient(bundleGreen1).entry.first().resource as ServiceRequest
        val patientId4 = servReqUzi4.subject!!.id!!
        val servReqUzi5 = QueRequests.createPatient(bundleGreen2).entry.first().resource as ServiceRequest
        val patientId5 = servReqUzi5.subject!!.id!!
        val servReqUzi6 = QueRequests.createPatient(bundleRed3).entry.first().resource as ServiceRequest
        val patientId6 = servReqUzi6.subject!!.id!!
    }

    @Test
    fun cancelEntering() {
        val servReq1 = Helpers.createServiceRequestResource("B03.016.002ГМУ_СП")
        val bundleRed1 = bundle("1111", "RED", listOf(servReq1))
        val bundleRed2 = bundle("1112", "RED", listOf(servReq1))
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servReqPatient1 = QueRequests.createPatient(bundleRed1).entry.first().resource as ServiceRequest
        val patientId1 = servReqPatient1.subject!!.id!!
        val servReqPatient2 = QueRequests.createPatient(bundleRed2).entry.first().resource as ServiceRequest
        val patientId2 = servReqPatient2.subject!!.id!!
        //пациент вошел в кабинет
        QueRequests.patientEntered(createListResource(patientId1, office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //отмена входа пациента, пациент должен вернуться первым в очередь
        QueRequests.cancelEntering(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun patientShouldBeSentInNextOfficeAfterAddingObservation() {
        val observationCode = "B03.016.002ГМУ_СП"
        val observationCode2 = "A09.28.029ГМУ_СП"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode),
                Helpers.createServiceRequestResource(observationCode2)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101))

        //создание пациента
        val actServRequests = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServRequests)

        //пациент вошел
        val patientEnteredListResource = Helpers.createListResource(patientId, office101)
        val actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)
        val servRequstId = actServicesInOffice.find { it.code.code() == observationCode}?.id!!

        //создание Observation
        val obs = Helpers.createObservation(
                code = Observations.observationCode,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId
        )
        QueRequests.createObservation(obs)

        //пациент вышел и должен быть направлен в следующий кабинет
        QueRequests.patientLeft(referenceToLocation(office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office104, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }
}