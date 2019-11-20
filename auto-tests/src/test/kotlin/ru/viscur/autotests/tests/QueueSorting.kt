package ru.viscur.autotests.tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.utils.*

//@Disabled("Debug purposes only")
class QueueSorting {

    companion object {
        val office116 = "Office:116"
        val office117 = "Office:117"
        val office101 = "Office:101"
        val office130 = "Office:130"
        val office119 = "Office:119"
        val redZone = "Office:RedZone"

        val observCode = "B03.016.002ГМУ_СП"
        val observationOfSurgeonCode = "СтХир"
        val observation1Office101 = "B03.016.002ГМУ_СП"
        val observation2Office101 = "A09.20.003ГМУ_СП"
    }

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun GreenYellowSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun YellowRedSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1121", "YELLOW", servRequests)
        val bundle2 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun GreenRedSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun GreenGreenSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun YellowYellowSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "YELLOW", servRequests)
        val bundle2 = bundle("1122", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun RedRedSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "RED", servRequests)
        val bundle2 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun GreenYellowRedSorting() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingWithGoingToObservation() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingWithOnObservation() {
        QueRequests.officeIsReady(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        QueRequests.patientEntered(Helpers.createListResource(patientId1, office101))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun samePriorityOfficeSorting() {
        val observationOffice130 = "A05.10.002"
        val observationOffice119 = "A06.28.002"
        val servRequests1 = listOf(
                Helpers.createServiceRequestResource(observationOffice119)
        )
        val servRequests2 = listOf(
                Helpers.createServiceRequestResource(observationOffice130),
                Helpers.createServiceRequestResource(observationOffice119)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests1)
        val bundle2 = Helpers.bundle("1123", "RED", servRequests2)
        QueRequests.officeIsBusy(referenceToLocation(office130))
        QueRequests.officeIsBusy(referenceToLocation(office119))
        //2 пациента, одному только в 119, второму в 119 и 130, приоритеты одинаковые у кабинетов
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //первый пациент должен попасть в 119, второй тоже в 119, но 119 занят очередь направляет в 202(такой же приоритет)
        checkQueueItems(listOf(
                QueueItemsOfOffice(office119, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office130, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun lessWaitingDurationSorting() {
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle1 = bundle("1111", "RED", listOf(servReq1))
        val servReq2 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle2 = bundle("1112", "RED", listOf(servReq2))
        //добавление 2 пациентов на узи (2 кабинета на выбор)
        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        //проверка что второго пациента направляет в другую очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun lessQueueCountOfficeSorting() {
        val servRequest = listOf(Helpers.createServiceRequestResource("A04.16.001"))
        val bundleRed1 = bundle("1111", "RED", servRequest)
        val bundleRed2 = bundle("1112", "RED", servRequest)
        val bundleRed3 = bundle("1113", "RED", servRequest)
        val bundleYellow1 = bundle("1114", "YELLOW", servRequest)
        val bundleGreen1 = bundle("1117", "GREEN", servRequest)
        val bundleGreen2 = bundle("1118", "GREEN", servRequest)
        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))
        //создание очереди для проверки сортировки в кабинет с меньшим количеством пациентов при равном времени ожидания
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundleYellow1).resources(ResourceType.ServiceRequest))
        val patientId4 = patientIdFromServiceRequests(QueRequests.createPatient(bundleGreen1).resources(ResourceType.ServiceRequest))
        val patientId5 = patientIdFromServiceRequests(QueRequests.createPatient(bundleGreen2).resources(ResourceType.ServiceRequest))
        val patientId6 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed3).resources(ResourceType.ServiceRequest))
        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId6, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId5, PatientQueueStatus.IN_QUEUE)

                ))
        ))
    }

    @Test
    fun sortingWhenQueueCorrected() {
        QueRequests.officeIsBusy(referenceToLocation(office101))
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observCode)
        )
        val bundle1 = bundle("1111", "GREEN", servRequests)
        val bundle2 = bundle("1112", "YELLOW", servRequests)
        val bundle3 = bundle("1113", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        QueRequests.setPatientFirst(Helpers.createListResource(patientId1, office101))
        //проверка корректного формирования очереди
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingAfterCancellingServiceRequestsInOffice() {
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101),
                Helpers.createServiceRequestResource(observation2Office101)
        )
        val bundle = Helpers.bundle("7879", "RED", servRequests)
        QueRequests.officeIsBusy(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(redZone))

        val serviceRequests= QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequests)
        //пациент в очереди в 101
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101),
                ServiceRequestInfo(code = observation2Office101, locationId = office101),
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = redZone)
        ))

        QueRequests.cancelOfficeServiceRequests(patientId, office101)
        //после удаления Service Requests в 101м пациента должно убрать из очереди
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observation2Office101, locationId = office101, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observationOfSurgeonCode, locationId = redZone)
        ))
        checkQueueItems(listOf(
                QueueItemsOfOffice(redZone, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)

                ))
        ))
    }

    @Test
    fun sortingAfterAddingObservation() {
        val observationCode = "B03.016.002ГМУ_СП"
        val observationCode2 = "A04.16.001"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationCode),
                Helpers.createServiceRequestResource(observationCode2)
        )
        val bundle1 = Helpers.bundle("1122", "RED", servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101))
        QueRequests.officeIsBusy(referenceToLocation(office116))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
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
        QueRequests.patientLeft(Helpers.createListResource(patientId, office101))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingAfterCancellingServiceRequest() {
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
        //проверка, что пациент попал в очередь в 116 после удаления Service Request в 101
        QueRequests.cancelOfficeServiceRequests(patientId, office101)
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun recalcSortingFinishedObservation() {
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
        QueRequests.setQueueResortingConfig(true)
        QueRequests.patientEntered(Helpers.createListResource(patientId1, office101))
        val obs = Helpers.createObservation(
                code = observationCode1,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = responsePatient1.first().id
        )
        QueRequests.createObservation(obs)
        QueRequests.patientLeft(Helpers.createListResource(patientId1, office101))

        //по маршрутному листу в 116, очередь должна оправить в 117 при включенной настройке перерасчета очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        QueRequests.setQueueResortingConfig(false)
    }

    @Test
    fun recalcSortingCabinetIsClosed() {
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
        //офис закрыт, пациент должен быть перенаправлен в 117 при включенной настройке перерасчета очереди
        QueRequests.setQueueResortingConfig(true)
        QueRequests.officeIsClosed(referenceToLocation(office116))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        QueRequests.setQueueResortingConfig(false)
    }
}
