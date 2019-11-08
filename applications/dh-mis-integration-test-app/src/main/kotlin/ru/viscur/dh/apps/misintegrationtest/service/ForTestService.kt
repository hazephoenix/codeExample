package ru.viscur.dh.apps.misintegrationtest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.util.BaseTestCase
import ru.viscur.dh.apps.misintegrationtest.util.ServiceRequestSimple
import ru.viscur.dh.datastorage.api.ConceptService
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
        //кабинеты busy
        officeService.all().forEach {
            resourceService.update(ResourceType.Location, it.id) {
                status = defaultOfficeStatus
            }
        }
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

    private fun updateOfficeStatuses() {
        queueManagerService.queueItems().groupBy { it.location.id!! }.forEach { officeId, items ->
            items.sortedBy { it.onum }.forEachIndexed { index, item ->
                if (index == 0) {
                    val officeStatus = when (item.patientQueueStatus) {
                        PatientQueueStatus.ON_OBSERVATION -> LocationStatus.OBSERVATION
                        PatientQueueStatus.IN_QUEUE -> defaultOfficeStatus
                        PatientQueueStatus.GOING_TO_OBSERVATION -> LocationStatus.WAITING_PATIENT
                        else -> defaultOfficeStatus
                    }
                    if (officeStatus != defaultOfficeStatus) {
                        resourceService.update(ResourceType.Location, officeId) {
                            status = officeStatus
                        }
                    }
                }
            }
        }
    }

    fun formQueueInfo(): String {
        val queueItems = queueManagerService.queueItems()
        return queueItems.sortedBy { it.location.id }.groupBy { it.location.id }.map { (locationId, items) ->
            "$locationId:\n  ${items.sortedBy { it.onum }.joinToString("\n  ") { "${it.onum}. ${it.patientQueueStatus}, ${it.severity}, ${it.estDuration}, ${it.subject.id}" }}"
        }.joinToString("\n")
    }

    fun createPatientWithQueueItem(severity: Severity, servReqs: List<ServiceRequestSimple>? = null, officeId: String, queueStatus: PatientQueueStatus, index: Int): String {
        val patientId = createPatient(severity = severity, officeId = officeId, queueStatus = queueStatus, servReqs = servReqs)
        resourceService.create(QueueItem(
                onum = index,
                subject = referenceToPatient(patientId),
                estDuration = 5 * SECONDS_IN_MINUTE,
                location = referenceToLocation(officeId)
        ))
        return patientId
    }

    fun createPatient(severity: Severity, servReqs: List<ServiceRequestSimple>? = null,
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
}