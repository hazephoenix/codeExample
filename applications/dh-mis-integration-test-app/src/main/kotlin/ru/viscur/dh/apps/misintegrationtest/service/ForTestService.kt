package ru.viscur.dh.apps.misintegrationtest.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.util.BaseTestCase
import ru.viscur.dh.apps.misintegrationtest.util.QueueOfOfficeSimple
import ru.viscur.dh.apps.misintegrationtest.util.ServiceRequestSimple
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 08.11.2019 11:44 by SherbakovaMA
 *
 * todo
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
        val servRequests = servReqs?.map { Helpers.createServiceRequestResource(servRequestCode = it.code, patientId = patientId, status = it.status) }
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
}