package ru.viscur.dh.apps.misintegrationtest.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.util.BaseTestCase
import ru.viscur.dh.apps.misintegrationtest.util.QueueOfOfficeSimple
import ru.viscur.dh.apps.misintegrationtest.util.ServiceRequestSimple
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 08.11.2019 11:44 by SherbakovaMA
 *
 * Сервис для тестов
 */
@Service
class ForTestService {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var resourceService: ResourceService

    @Autowired
    lateinit var officeService: OfficeService

    @Autowired
    lateinit var locationService: LocationService

    @Autowired
    lateinit var patientService: PatientService

    @Autowired
    lateinit var conceptService: ConceptService

    @Autowired
    lateinit var receptionService: ReceptionService

    @Autowired
    lateinit var serviceRequestService: ServiceRequestService

    companion object {
        private val defaultOfficeStatus = LocationStatus.BUSY
    }

    fun cleanDb() {
        resourceService.deleteAll(ResourceType.ServiceRequest)
        resourceService.deleteAll(ResourceType.CarePlan)
        resourceService.deleteAll(ResourceType.Patient)
        resourceService.deleteAll(ResourceType.Consent)
        resourceService.deleteAll(ResourceType.Claim)
        resourceService.deleteAll(ResourceType.ClinicalImpression)
        resourceService.deleteAll(ResourceType.DiagnosticReport)
        resourceService.deleteAll(ResourceType.Observation)
        resourceService.deleteAll(ResourceType.ClinicalImpression)
        resourceService.deleteAll(ResourceType.QuestionnaireResponse)
        resourceService.deleteAll(ResourceType.QueueItem)
    }

    fun prepareDb(case: BaseTestCase): String {
        //все для очереди
        case.queue.forEach { queueOfOffice ->
            queueOfOffice.items.forEachIndexed { index, item ->
                val patientId = createPatient(severity = item.severity!!, officeId = queueOfOffice.officeId, queueStatus = item.status!!)
                resourceService.create(QueueItem(
                        onum = index,
                        subject = referenceToPatient(patientId),
                        estDuration = item.estDuration,
                        location = referenceToLocation(queueOfOffice.officeId)
                ))
            }
        }
        updateOfficeStatuses()
        //все для маршрутного листа пациента
        return createPatient(severity = case.carePlan.severity, servReqs = case.carePlan.servReqs)
    }

    fun updateOfficeStatuses() {
        //кабинеты busy
        officeService.all().forEach {
            resourceService.update(ResourceType.Location, it.id) {
                status = defaultOfficeStatus
            }
        }
        queueManagerService.queueItems().groupBy { it.location.id!! }.forEach { officeId, items ->
            val officeStatus =
                    when {
                        items.any { it.patientQueueStatus == PatientQueueStatus.ON_OBSERVATION } -> LocationStatus.OBSERVATION
                        items.any { it.patientQueueStatus == PatientQueueStatus.GOING_TO_OBSERVATION } -> LocationStatus.WAITING_PATIENT
                        else -> LocationStatus.BUSY
                    }
            resourceService.update(ResourceType.Location, officeId) {
                status = officeStatus
            }
        }
    }

    fun formQueueInfo(): String {
        val queueItems = queueManagerService.queueItems()
        return queueItems.sortedBy { it.location.id }.groupBy { it.location.id }.map { (locationId, items) ->
            "$locationId:\n  ${items.sortedBy { it.onum }.joinToString("\n  ") { "${it.onum}. ${it.patientQueueStatus}, ${it.severity}, ${it.estDuration}, ${it.subject.id}" }}"
        }.joinToString("\n")
    }

    fun createPatientWithQueueItem(severity: Severity = Severity.GREEN, servReqs: List<ServiceRequestSimple>? = null, officeId: String, queueStatus: PatientQueueStatus, index: Int): String {
        val patientId = createPatient(severity = severity, officeId = officeId, queueStatus = queueStatus, servReqs = servReqs)
        resourceService.create(QueueItem(
                onum = index,
                subject = referenceToPatient(patientId),
                estDuration = 5 * SECONDS_IN_MINUTE,
                location = referenceToLocation(officeId)
        ))
        return patientId
    }

    fun createPatient(severity: Severity = Severity.GREEN, servReqs: List<ServiceRequestSimple>? = null,
                      officeId: String? = null,
                      queueStatus: PatientQueueStatus = PatientQueueStatus.READY): String {
        val patientId = resourceService.create(Helpers.createPatientResource(enp = genId(), queueStatus = queueStatus)).id
        val servRequests = servReqs?.toServiceRequests(patientId)
                ?: run {
                    var servRequestCode = resourceService.byId(ResourceType.Location, officeId!!).extension!!.observationType!!.first().code
                    val subTypes = conceptService.byParent(ValueSetName.OBSERVATION_TYPES, servRequestCode)
                    if (subTypes.isNotEmpty()) {
                        servRequestCode = subTypes.first().code
                    }
                    listOf(Helpers.createServiceRequestResource(
                            servRequestCode,
                            patientId
                    ))
                }
        val createdServRequests = servRequests.map { resourceService.create(it) }
        val carePlan = resourceService.create(Helpers.createCarePlan(patientId, createdServRequests))
        val diagnosticReport = resourceService.create(Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", patientId = patientId))
        val questionnaireResponse = resourceService.create(Helpers.createQuestResponseResource(severity = severity.name, patientId = patientId))
        resourceService.create(Helpers.createClinicalImpression(patientId, listOf(Reference(questionnaireResponse), Reference(carePlan), Reference(diagnosticReport))))
        queueManagerService.calcServiceRequestExecOrders(patientId)

        if (!servReqs.isNullOrEmpty()) {
            servReqs.forEachIndexed { index, sr ->
                servRequests.find { it.code.code() == sr.code && it.status == ServiceRequestStatus.active }?.let {
                    resourceService.update(ResourceType.ServiceRequest, it.id) {
                        extension!!.executionOrder = index
                    }
                }
            }
        }
        return patientId
    }

    private fun List<ServiceRequestSimple>.toServiceRequests(patientId: String) =
            this.map { Helpers.createServiceRequestResource(servRequestCode = it.code, patientId = patientId, status = it.status) }

    fun registerPatient(servReqs: List<ServiceRequestSimple>, severity: Severity = Severity.GREEN): List<ServiceRequest> {
        val patient = Helpers.createPatientResource(enp = genId())
        val bodyWeight = Helpers.createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = Helpers.paramedicId)
        val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity.name)
        val personalDataConsent = Helpers.createConsentResource()
        val diagnosticReport = Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = Helpers.paramedicId)
        val list = Helpers.createPractitionerListResource(Helpers.surgeonId)
        val claim = Helpers.createClaimResource()

        val bundle = Bundle(entry = listOf(
                BundleEntry(patient),
                BundleEntry(diagnosticReport),
                BundleEntry(bodyWeight),
                BundleEntry(personalDataConsent),
                BundleEntry(list),
                BundleEntry(claim),
                BundleEntry(questionnaireResponseSeverityCriteria)
        ) + servReqs.toServiceRequests(patient.id).map { BundleEntry(it) })

        return receptionService.registerPatient(bundle)
    }


    fun checkQueueItems(itemsByOffices: List<QueueOfOfficeSimple>) {
        val actQueueItems = queueManagerService.queueItems()
        val allItems = itemsByOffices.flatMap { it.items }
        val itemsStr = itemsToStr(itemsByOffices, actQueueItems)
        //количество в принципе разное
        assertEquals(allItems.size, actQueueItems.size, "wrong number of queueItems. $itemsStr")
        itemsByOffices.forEach { byOffice ->
            val officeId = byOffice.officeId
            val office = locationService.byId(officeId)
            assertEquals(byOffice.officeStatus, office.status, "wrong status of office $officeId. $itemsStr")
            byOffice.items.forEachIndexed { index, queueItemInfo ->
                //поиск соответствующего элемента в текущих
                val foundInAct = actQueueItems.filter { it.subject.id == queueItemInfo.patientId && it.location.id == officeId }
                assertEquals(1, foundInAct.size, "not found (or found multiple items) of $queueItemInfo. $itemsStr")
                val foundItem = foundInAct.first()
                //проверка правильности данных в найденном
                assertEquals(index, foundItem.onum, "wrong onum of $queueItemInfo. $itemsStr")
                val patientId = queueItemInfo.patientId!!
                val patient = patientService.byId(patientId)
                assertEquals(queueItemInfo.status, patient.extension.queueStatus, "wrong patientStatus with id '$patientId'. $itemsStr")
                assertEquals(queueItemInfo.status, foundItem.patientQueueStatus, "wrong queueItem.patientQueueStatus with id '$patientId'. $itemsStr")
            }
        }
    }

    /**
     * Проверка правильности назначений определенного пациента
     * (не полная проверка назначений всех пациентов, а только в разрезе одного пациента)
     */
    fun checkServiceRequestsOfPatient(patientId: String, servReqInfos: List<ServiceRequestSimple>) {
        val actServRequests = serviceRequestService.all(patientId)
        checkServiceRequests(patientId, servReqInfos, actServRequests)
    }

    /**
     * Сравнивает 2 списка назначений: [servReqInfos] ожидаемый и [actServRequests] текущий
     * пациента [patientId]
     */
    fun checkServiceRequests(patientId: String, servReqInfos: List<ServiceRequestSimple>, actServRequests: List<ServiceRequest>) {
        val servReqsStr = servReqsToString(patientId, servReqInfos, actServRequests)
        //количество в принципе разное
        assertEquals(servReqInfos.size, actServRequests.size, "wrong number of servRequests. $servReqsStr")
        servReqInfos.forEachIndexed { index, servReqInfo ->
            val foundInAct = actServRequests.filter { it.code.code() == servReqInfo.code }
            assertEquals(1, foundInAct.size, "not found (or found multiple items) with code '${servReqInfo.code}' of $servReqInfo. $servReqsStr")
            val foundItem = foundInAct.first()
            //проверка правильности данных в найденном
            assertEquals(patientId, foundItem.subject?.id, "wrong patientId of $servReqInfo. $servReqsStr")
            assertEquals(servReqInfo.status, foundItem.status, "wrong status of $servReqInfo. $servReqsStr")
            servReqInfo.locationId?.run { assertEquals(servReqInfo.locationId, foundItem.locationReference?.first()?.id, "wrong locationId of $servReqInfo. $servReqsStr") }
            assertEquals(index, foundItem.extension?.executionOrder, "wrong executionOrder of $servReqInfo. $servReqsStr")
        }
    }

    private fun itemsToStr(itemsByOffices: List<QueueOfOfficeSimple>, actQueueItems: List<QueueItem>): String {
        val actByOffices = actQueueItems.groupBy { it.location.id!! }
        return "\n\nexp queue:\n" +
                itemsByOffices.joinToString("\n") { byOffice -> byOffice.officeId + ":\n  " + byOffice.items.mapIndexed { index, queueItemInfo -> "$index. $queueItemInfo" }.joinToString("\n  ") } +
                "\n\nactual queue:\n" +
                actByOffices.map { (officeId, items) ->
                    officeId + "\n  " + items.sortedBy { it.onum }.joinToString("\n  ")
                }.joinToString("\n  ") +
                "\n\n"
    }

    fun servReqsToString(patientId: String, servReqs: List<ServiceRequestSimple>, actServRequests: List<ServiceRequest>): String {
        return "\n\nfor patient '$patientId'\nexp servRequests:\n  " +
                servReqs.joinToString("\n  ") { it.toString() } +
                "\n\nactual:\n  " +
                actServRequests.joinToString("\n  ") { "code: " + it.code.code() + ", status: " + it.status + ", locationId: " + it.locationReference?.first()?.id } +
                "\n\n"
    }
}