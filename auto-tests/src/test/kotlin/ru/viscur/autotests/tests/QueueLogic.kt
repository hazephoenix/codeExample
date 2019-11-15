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

    @Test
    fun patientForceInviteToOffice() {
        val servReq1 = Helpers.createServiceRequestResource(observCode)
        val bundle = bundle("1122", "RED", listOf(servReq1))

        QueRequests.officeIsReady(referenceToLocation(office101))
        val patientId = patientIdFromServiceRequests( QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest))

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
    fun cancelEntering() {
        val servReq1 = Helpers.createServiceRequestResource(observCode)
        val bundleRed1 = bundle("1111", "RED", listOf(servReq1))
        val bundleRed2 = bundle("1112", "RED", listOf(servReq1))
        QueRequests.officeIsReady(referenceToLocation(office101))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))

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
        val observationCode2 = "A04.16.001"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode),
                Helpers.createServiceRequestResource(observationCode2)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))


        //создание пациента
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

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
        QueRequests.patientLeft(createListResource(patientId, office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun patientShouldBeSentInNextOfficeAfterCancellingServiceRequest() {
        //Todo доделать когда заработает апи
        val observationCode1 = "B03.016.002ГМУ_СП"
        val observationCode2 = "A04.16.001"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode1),
                Helpers.createServiceRequestResource(observationCode2)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        //пациент стоит в очереди в 101
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //попадание в очередь в следующий кабинет после удаления Service Request в 101
        QueRequests.cancelOfficeServiceRequests(patientId, office101)
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun closingCabinet() {
        val observation101Office = "B03.016.002ГМУ_СП"
        val observation116Office = "A04.16.001"
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation101Office),
                Helpers.createServiceRequestResource(observation101Office)
        )
        val bundleRed1 = bundle("1111", "RED", servRequests)
        val bundleRed2 = bundle("1112", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        QueRequests.officeIsClosed(referenceToLocation(office101))
        checkQueueItems(listOf())
    }

    @Test
    fun GettingInAnotherQueueIfCabinetIsClosed() {
        //не работает
        val observation116Office = "A04.16.001"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation116Office)
        )

        val bundleRed1 = bundle("1111", "RED", servRequests)
        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        //пациент стоит в 116
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        //офис закрыт, пациент должен быть перенаправлен в 117
        QueRequests.officeIsClosed(referenceToLocation(office116))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun queueDisbandmentWithAnotherServiceRequest() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servReq1 = listOf(
                Helpers.createServiceRequestResource(observCode),
                Helpers.createServiceRequestResource(observCode)
        )
        val bundleRed1 = bundle("1111", "RED", servReq1)
        val bundleRed2 = bundle("1112", "RED", servReq1)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        QueRequests.officeIsClosed(referenceToLocation(office101))
        checkQueueItems(listOf())
    }


    @Test
    fun gettingInCorrectQueueAfterFinishingObservation() {
        //Todo доделать, должен работать при включенном переопределении очереди
        val observationCode1 = "B03.016.002ГМУ_СП"
        val observationCode2 = "A04.16.001"
        val servRequests1 = listOf(
                Helpers.createServiceRequestResource(observationCode1),
                Helpers.createServiceRequestResource(observationCode2)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationCode2)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests1)
        val bundle2 = Helpers.bundle("1123", "RED", servRequests2)

        QueRequests.officeIsReady(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))

        val responsePatient1 = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId1 = patientIdFromServiceRequests(responsePatient1)
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        QueRequests.patientEntered(createListResource(patientId1, office101))
        val obs = Helpers.createObservation(
                code = observationCode1,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = responsePatient1.first().id
        )
        val actObs = QueRequests.createObservation(obs)
        QueRequests.patientLeft(createListResource(patientId1, office101))
        //по маршрутному листу в 116, очередь должна оправить в 117 после корретировки
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
    fun settingPatientAsFirst () {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1111", "GREEN", servRequests)
        val bundle2 = bundle("1112", "YELLOW", servRequests)
        val bundle3 = bundle("1113", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        QueRequests.setPatientFirst(Helpers.createListResource(patientId1, office101))
        //Зеленый пациент должен быть переставлен первым в очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun twoPatientsGoingToObservation() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1111", "GREEN", servRequests)
        val bundle2 = bundle("1112", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        QueRequests.inviteNextPatientToOffice(Helpers.createListResource(patientId2, office101))

        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId2, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }
}