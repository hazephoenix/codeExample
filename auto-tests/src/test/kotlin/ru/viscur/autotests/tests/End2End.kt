package ru.viscur.autotests.tests

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import ru.viscur.autotests.dto.*
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_101
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION1_OFFICE_116
import ru.viscur.autotests.utils.Constants.Companion.OBSERVATION_OF_SURGEON
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_116_ID
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_139_ID
import ru.viscur.autotests.utils.Constants.Companion.RED_ZONE_ID
import ru.viscur.autotests.utils.*
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createDiagnosticReportResource
import ru.viscur.autotests.utils.Helpers.Companion.createEncounter
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.Helpers.Companion.surgeonId
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*

@Disabled("Debug purposes only")
class End2End {

    @BeforeEach
    fun init() {
        QueRequests.deleteQue()
    }

    @Test
    fun patientServiceRequestAddingCycle() {

        val bundle = bundle("7879", "RED")
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_139_ID))
        val responseBundle = QueRequests.createPatient(bundle)

        //проверка наличия и количества Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        val actServiceRequests = responseBundle.resources(ResourceType.ServiceRequest)
        val patientId = patientIdFromServiceRequests(actServiceRequests)
        QueRequests.invitePatientToOffice(createListResource(patientId, OFFICE_139_ID))

        checkQueueItems(
            listOf(
                QueueItemsOfOffice(
                    OFFICE_139_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                    )
                )
            )
        )

        //пациент вошел в кабинет
        QueRequests.officeIsReady(referenceToLocation(OFFICE_139_ID))
        val patientEnteredListResource = createListResource(patientId, OFFICE_139_ID)
        QueRequests.patientEntered(patientEnteredListResource)

        //добавление ответственным дополнительного Service Request
        val additionalServiceRequests = listOf(
            createServiceRequestResource(OBSERVATION1_OFFICE_101, patientId)
        )
        val bundleForExamin = Bundle(
            entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        QueRequests.officeIsBusy(referenceToLocation(OFFICE_101_ID))
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //проверка, что в CarePlan добавлен ServiceRequest, пациент должен снова встать в очередь в другой кабинет
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")
        checkQueueItems(
            listOf(
                QueueItemsOfOffice(
                    OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.IN_QUEUE)
                    )
                )
            )
        )
        checkServiceRequestsOfPatient(
            patientId, listOf(
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID),
                ServiceRequestInfo(code = OBSERVATION1_OFFICE_101, locationId = OFFICE_101_ID)
            )
        )
        checkObservationsOfPatient(patientId, listOf())
    }

    @Test
    fun patientClaimCompletingFullCycle() {
        //создание пациента с 3 разными по приоритету обследованиями
        val patientServiceRequests = listOf(
            createServiceRequestResource(OBSERVATION1_OFFICE_101),
            createServiceRequestResource(OBSERVATION1_OFFICE_116)
        )
        val patientBundle = bundle("1333", "RED", patientServiceRequests)
        val responseBundle = QueRequests.createPatient(patientBundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))
        QueRequests.officeIsReady(referenceToLocation(OFFICE_101_ID))
        QueRequests.officeIsReady(referenceToLocation(OFFICE_116_ID))
        QueRequests.officeIsReady(referenceToLocation(OFFICE_139_ID))

        assertEquals(3, responseBundle.entry.size, "wrong number of service request")

        //прохождение всех обследований поочередно
        //office101
        val servRequestOf101office = QueRequests.patientEntered(
            createListResource(
                patientId,
                OFFICE_101_ID
            )
        ).find { it.code.code() == OBSERVATION1_OFFICE_101 } as ServiceRequest
        checkQueueItems(
            listOf(
                QueueItemsOfOffice(
                    OFFICE_101_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                    )
                )
            )
        )
        val obs = createObservation(
            code = OBSERVATION1_OFFICE_101,
            status = ObservationStatus.final,
            basedOnServiceRequestId = servRequestOf101office.id,
            valueString = "результат анализа"
        )
        QueRequests.createObservation(obs)
        QueRequests.patientLeft(createListResource(patientId, OFFICE_101_ID))

        //office116
        val servRequestOf116office = QueRequests.patientEntered(
            createListResource(
                patientId,
                OFFICE_116_ID
            )
        ).find { it.code.code() == OBSERVATION1_OFFICE_116 } as ServiceRequest
        checkQueueItems(
            listOf(
                QueueItemsOfOffice(
                    OFFICE_116_ID, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.ON_OBSERVATION)
                    )
                )
            )
        )
        val obs3 = createObservation(
            code = OBSERVATION1_OFFICE_116,
            status = ObservationStatus.final,
            basedOnServiceRequestId = servRequestOf116office.id,
            valueString = "результат анализа"
        )
        QueRequests.createObservation(obs3)
        QueRequests.patientLeft(createListResource(patientId, OFFICE_116_ID))

        //проверка что все обследования, кроме ответственного пройдены
        checkServiceRequestsOfPatient(
            patientId, listOf(
                ServiceRequestInfo(
                    code = OBSERVATION1_OFFICE_101,
                    locationId = OFFICE_101_ID,
                    status = ServiceRequestStatus.completed
                ),
                ServiceRequestInfo(
                    code = OBSERVATION1_OFFICE_116,
                    locationId = OFFICE_116_ID,
                    status = ServiceRequestStatus.completed
                ),
                ServiceRequestInfo(code = OBSERVATION_OF_SURGEON, locationId = RED_ZONE_ID)
            )
        )

        //office 139 осмотр ответственного и завершение маршрутного листа с госпитализацией
        QueRequests.invitePatientToOffice(createListResource(patientId, OFFICE_139_ID))
        val servRequestOfResp = QueRequests.patientEntered(
            createListResource(
                patientId,
                OFFICE_139_ID
            )
        ).find { it.code.code() == OBSERVATION_OF_SURGEON } as ServiceRequest
        val obsOfRespPract = createObservation(
            code = OBSERVATION_OF_SURGEON,
            valueString = "состояние удовлетворительное",
            practitionerId = servRequestOfResp.performer?.first()?.id()!!,
            basedOnServiceRequestId = servRequestOfResp.id,
            status = ObservationStatus.final,
            patientId = patientId
        )
        val diagnosticReportOfResp = createDiagnosticReportResource(
            diagnosisCode = "A16",
            practitionerId = surgeonId,
            status = DiagnosticReportStatus.final,
            patientId = patientId
        )
        val encounter = createEncounter(hospitalizationStr = "Клиники СибГму", patientId = patientId)
        val bundleForExamination = Bundle(
            entry = listOf(
                BundleEntry(obsOfRespPract),
                BundleEntry(diagnosticReportOfResp),
                BundleEntry(encounter)
            )
        )
        val completedClinicalImpression = QueRequests.completeExamination(bundleForExamination)

        //проверка, что маршрутный лист пациента завершен и он удален из системы очередь
        assertEquals(
            ClinicalImpressionStatus.completed,
            completedClinicalImpression.status,
            "wrong status of ClinicalImpression"
        )
        checkQueueItems(listOf())
        checkServiceRequestsOfPatient(patientId, listOf())
        checkObservationsOfPatient(patientId, listOf())
    }
}