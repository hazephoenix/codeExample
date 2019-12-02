package ru.viscur.autotests.tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.observation1Office101
import ru.viscur.autotests.utils.Constants.Companion.observation1Office116
import ru.viscur.autotests.utils.Constants.Companion.observation1Office149
import ru.viscur.autotests.utils.Constants.Companion.observation1Office202
import ru.viscur.autotests.utils.Constants.Companion.observation2Office101
import ru.viscur.autotests.utils.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.utils.Constants.Companion.office101Id
import ru.viscur.autotests.utils.Constants.Companion.office116Id
import ru.viscur.autotests.utils.Constants.Companion.office117Id
import ru.viscur.autotests.utils.Constants.Companion.office149Id
import ru.viscur.autotests.utils.Constants.Companion.office202Id
import ru.viscur.autotests.utils.Constants.Companion.redZoneId
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
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

    @BeforeEach
    fun init() {
        QueRequests.setQueueResortingConfig(false)
        QueRequests.deleteQue()
    }

    @Test
    fun GreenYellowSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun YellowRedSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1121", "YELLOW", servRequests)
        val bundle2 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun GreenRedSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun GreenGreenSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun YellowYellowSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "YELLOW", servRequests)
        val bundle2 = bundle("1122", "YELLOW", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun RedRedSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "RED", servRequests)
        val bundle2 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun GreenYellowRedSorting() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1121", "YELLOW", servRequests)
        val bundle3 = bundle("1122", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingWithGoingToObservation() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.GOING_TO_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingWithOnObservation() {
        //создание очереди
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)
        val bundle2 = bundle("1122", "YELLOW", servRequests)
        val bundle3 = bundle("1123", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        QueRequests.patientEntered(createListResource(patientId1, office101Id))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.ON_OBSERVATION),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun samePriorityOfficeSorting() {
        //создание очереди
        val servRequests1 = listOf(
                createServiceRequestResource(observation1Office149)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(observation1Office149),
                createServiceRequestResource(observation1Office202)
        )
        val bundle1 = bundle("1122", "RED", servRequests1)
        val bundle2 = bundle("1123", "RED", servRequests2)
        QueRequests.officeIsBusy(referenceToLocation(office149Id))
        QueRequests.officeIsBusy(referenceToLocation(office202Id))

        //2 пациента, одному только в 149, второму в 202 и 149, приоритеты одинаковые у кабинетов
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //первый пациент должен попасть в 149, второй тоже в 149, но он занят очередь направляет в 202(такой же приоритет)
        checkQueueItems(listOf(
                QueueItemsOfOffice(office202Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office149Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun lessWaitingDurationSorting() {
        //создание очереди
        val servRequests = createServiceRequestResource(observation1Office116)
        val bundle1 = bundle("1111", "RED", listOf(servRequests))
        val bundle2 = bundle("1112", "RED", listOf(servRequests))

        //добавление 2 пациентов на узи (2 кабинета на выбор)
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        QueRequests.officeIsBusy(referenceToLocation(office117Id))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка что второго пациента направляет в другую очередь
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun lessQueueCountOfficeSorting() {
        //создание очереди для проверки сортировки в кабинет с меньшим количеством пациентов при равном времени ожидания
        val servRequests = listOf(createServiceRequestResource(observation1Office116))
        val bundleRed1 = bundle("1111", "RED", servRequests)
        val bundleRed2 = bundle("1112", "RED", servRequests)
        val bundleRed3 = bundle("1113", "RED", servRequests)
        val bundleYellow1 = bundle("1114", "YELLOW", servRequests)
        val bundleGreen1 = bundle("1117", "GREEN", servRequests)
        val bundleGreen2 = bundle("1118", "GREEN", servRequests)
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        QueRequests.officeIsBusy(referenceToLocation(office117Id))
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed2).resources(ResourceType.ServiceRequest))
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundleYellow1).resources(ResourceType.ServiceRequest))
        val patientId4 = patientIdFromServiceRequests(QueRequests.createPatient(bundleGreen1).resources(ResourceType.ServiceRequest))
        val patientId5 = patientIdFromServiceRequests(QueRequests.createPatient(bundleGreen2).resources(ResourceType.ServiceRequest))
        val patientId6 = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed3).resources(ResourceType.ServiceRequest))

        //проверка корректного формирования очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId6, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId4, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId5, PatientQueueStatus.IN_QUEUE)

                ))
        ))
    }

    @Test
    fun sortingWhenQueueCorrected() {
        //создание очереди
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1111", "GREEN", servRequests)
        val bundle2 = bundle("1112", "YELLOW", servRequests)
        val bundle3 = bundle("1113", "RED", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        QueRequests.setPatientFirst(createListResource(patientId1, office101Id))

        //проверка корректного формирования очереди
        val patientId3 = patientIdFromServiceRequests(QueRequests.createPatient(bundle3).resources(ResourceType.ServiceRequest))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId3, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE),
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingAfterCancellingServiceRequestsInOffice() {
        //создание очереди
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101),
                createServiceRequestResource(observation2Office101)
        )
        val bundle = bundle("7879", "RED", servRequests)
        QueRequests.officeIsBusy(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(redZoneId))

        val serviceRequests = QueRequests.createPatient(bundle).resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(serviceRequests)

        //пациент в очереди в 101
        checkQueueItems(listOf(
                QueueItemsOfOffice(office101Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id),
                ServiceRequestInfo(code = observation2Office101, locationId = office101Id),
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId)
        ))

        QueRequests.cancelOfficeServiceRequests(patientId, office101Id)

        //после удаления Service Requests в 101 кабинет пациента должно убрать из очереди в него
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = observation1Office101, locationId = office101Id, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observation2Office101, locationId = office101Id, status = ServiceRequestStatus.cancelled),
                ServiceRequestInfo(code = observationOfSurgeon, locationId = redZoneId)
        ))
        checkQueueItems(listOf(
                QueueItemsOfOffice(redZoneId, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun sortingAfterAddingObservation() {
        //создание очереди
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101),
                createServiceRequestResource(observation1Office116)
        )
        val bundle1 = bundle("1122", "RED", servRequests)
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientEnteredListResource = createListResource(patientId, office101Id)
        val actServicesInOffice = QueRequests.patientEntered(patientEnteredListResource)
        val servRequstId = actServicesInOffice.find { it.code.code() == observation1Office101 }?.id!!

        //создание Observation
        val obs = createObservation(
                code = observation1Office101,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = servRequstId
        )
        QueRequests.createObservation(obs)

        //пациент вышел и должен быть направлен в следующий кабинет
        QueRequests.patientLeft(createListResource(patientId, office101Id))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun recalcSortingFinishedObservation() {
        //создание очереди и включение настройки перерасчета следующего кабинета
        val servRequests1 = listOf(
                createServiceRequestResource(observation1Office101),
                createServiceRequestResource(observation1Office116)
        )
        val servRequests2 = listOf(
                createServiceRequestResource(observation1Office116)
        )
        val bundle1 = bundle("1122", "RED", servRequests1)
        val bundle2 = bundle("1123", "RED", servRequests2)
        QueRequests.officeIsReady(referenceToLocation(office101Id))
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        QueRequests.officeIsBusy(referenceToLocation(office117Id))
        val responsePatient1 = QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest)
        val patientId1 = patientIdFromServiceRequests(responsePatient1)
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))
        QueRequests.setQueueResortingConfig(true)
        QueRequests.patientEntered(createListResource(patientId1, office101Id))
        val obs = createObservation(
                code = observation1Office101,
                status = ObservationStatus.registered,
                basedOnServiceRequestId = responsePatient1.first().id
        )
        QueRequests.createObservation(obs)
        QueRequests.patientLeft(createListResource(patientId1, office101Id))

        //по маршрутному листу в 116, очередь должна оправить в 117 при включенной настройке перерасчета очереди
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117Id, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office116Id, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        QueRequests.setQueueResortingConfig(false)
    }

    @Test
    fun recalcSortingCabinetIsClosed() {
        //создание очереди
        val servRequests = listOf(
                createServiceRequestResource(observation1Office116)
        )
        val bundleRed1 = bundle("1111", "RED", servRequests)
        QueRequests.officeIsBusy(referenceToLocation(office116Id))
        QueRequests.officeIsBusy(referenceToLocation(office117Id))
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundleRed1).resources(ResourceType.ServiceRequest))

        //пациент стоит в 116
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))

        //офис закрыт, пациент должен быть перенаправлен в 117 при включенной настройке перерасчета очереди
        QueRequests.setQueueResortingConfig(true)
        QueRequests.officeIsClosed(referenceToLocation(office116Id))
        checkQueueItems(listOf(
                QueueItemsOfOffice(office117Id, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                ))
        ))
        QueRequests.setQueueResortingConfig(false)
    }
}
