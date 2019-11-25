package ru.viscur.dh.apps.misintegrationtest.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.dto.ObservationDuration
import ru.viscur.dh.fhir.model.dto.QueueStatusDuration
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.QueueManagerService
import kotlin.math.abs

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

    @Autowired
    lateinit var reportService: ReportService

    companion object {
        private val defaultOfficeStatus = LocationStatus.BUSY

        private var counter = 0
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
        prepareQueue(case)
        updateOfficeStatuses()
        //все для маршрутного листа пациента
        return createPatient(severity = case.carePlan.severity, servReqs = case.carePlan.servReqs)
    }

    fun prepareQueue(case: BaseTestCase) {
        case.queue.forEach { queueOfOffice ->
            queueOfOffice.items.forEachIndexed { index, item ->
                val patientId = createPatient(severity = item.severity, officeId = queueOfOffice.officeId, queueStatus = item.status)
                resourceService.create(QueueItem(
                        onum = index,
                        subject = referenceToPatient(patientId),
                        estDuration = item.estDuration,
                        location = referenceToLocation(queueOfOffice.officeId),
                        queueCode = createQueueCode(item.severity)
                ))
            }
        }
    }

    fun updateOfficeStatuses() {
        //кабинеты busy
        officeService.all().forEach {
            resourceService.update(ResourceType.Location, it.id) {
                status = defaultOfficeStatus
                extension.nextOfficeForPatientsInfo = listOf()
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
                location = referenceToLocation(officeId),
                queueCode = createQueueCode(severity)
        ))
        return patientId
    }

    fun createPatient(severity: Severity = Severity.GREEN, servReqs: List<ServiceRequestSimple>? = null,
                      officeId: String? = null,
                      queueStatus: PatientQueueStatus = PatientQueueStatus.READY): String {
        val patientId = resourceService.create(Helpers.createPatientResource(enp = genId(), queueStatus = queueStatus)).id
        var servRequests = servReqs?.toServiceRequests(patientId)
                ?: run {
                    var servRequestCode = resourceService.byId(ResourceType.Location, officeId!!).extension.observationType!!.first().code
                    val subTypes = conceptService.byParent(ValueSetName.OBSERVATION_TYPES, servRequestCode)
                    if (subTypes.isNotEmpty()) {
                        servRequestCode = subTypes.first().code
                    }
                    listOf(Helpers.createServiceRequestResource(
                            servRequestCode,
                            patientId
                    ))
                }

        //добавление осмотра отв-ого если его нет
        val observationTypeOfResponsible = OBSERVATION_OF_SURGEON
        val serviceRequestOfResponsiblePr = (servRequests.find { it.code.code() == observationTypeOfResponsible }
                ?: ServiceRequest(code = observationTypeOfResponsible))
                .apply {
                    performer = listOf(referenceToPractitioner(Helpers.surgeonId))
                    subject = referenceToPatient(patientId)
                }
        servRequests = servRequests.filterNot { it.code.code() == observationTypeOfResponsible } + serviceRequestOfResponsiblePr
        val createdServRequests = servRequests.map { resourceService.create(it) }
        val carePlan = resourceService.create(Helpers.createCarePlan(patientId, createdServRequests))
        val diagnosticReport = resourceService.create(Helpers.createDiagnosticReportResource(diagnosisCode = "A00.0", patientId = patientId))
        val questionnaireResponseSeverityCriteria = resourceService.create(Helpers.createQuestResponseResource(severity = severity.name, patientId = patientId))
        val questionnaireResponseCommonInfo = resourceService.create(Helpers.createQuestResponseResourceWithCommonInfo(patientId = patientId))
        resourceService.create(Helpers.createClinicalImpression(patientId, severity, listOf(
                questionnaireResponseSeverityCriteria,
                questionnaireResponseCommonInfo,
                carePlan,
                diagnosticReport
        ).map { Reference(it) }))
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

    fun registerPatient(servReqs: List<ServiceRequestSimple>, severity: Severity = Severity.GREEN, diagnosisCode: String = "A00.0"): List<ServiceRequest> {
        val patient = Helpers.createPatientResource(enp = genId())
        val bodyWeight = Helpers.createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = Helpers.paramedicId, id = "ignored")
        val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity.name, id = "ignored")
        val questionnaireResponseCommonInfo = Helpers.createQuestResponseResourceWithCommonInfo(severity.name, id = "ignored")
        val personalDataConsent = Helpers.createConsentResource(id = "ignored")
        val diagnosticReport = Helpers.createDiagnosticReportResource(diagnosisCode = diagnosisCode, practitionerId = Helpers.paramedicId, id = "ignored")
        val mainSyndrome = Helpers.createDiagnosticReportResource(diagnosisCode = diagnosisCode, practitionerId = Helpers.paramedicId, status = DiagnosticReportStatus.mainSyndrome, id = "ignored")
        val listWithRespPractitioner = Helpers.createPractitionerListResource(Helpers.surgeonId, id = "ignored")
        val claim = Helpers.createClaimResource(id = "ignored")

        val bundle = Bundle(entry = (
                servReqs.toServiceRequests(patient.id) +
                        listWithRespPractitioner +
                        patient +
                        diagnosticReport +
                        mainSyndrome +
                        bodyWeight +
                        personalDataConsent +
                        claim +
                        questionnaireResponseSeverityCriteria +
                        questionnaireResponseCommonInfo
                ).map { BundleEntry(it) })

        return receptionService.registerPatient(bundle)
    }

    fun registerPatientForBandage(diagnosisCode: String = "A00.0"): List<ServiceRequest> {
        val patient = Helpers.createPatientResource(enp = genId())
        val personalDataConsent = Helpers.createConsentResource()
        val diagnosticReport = Helpers.createDiagnosticReportResource(diagnosisCode = diagnosisCode, practitionerId = Helpers.paramedicId)
        val mainSyndrome = Helpers.createDiagnosticReportResource(diagnosisCode = "A00.1", practitionerId = Helpers.paramedicId, status = DiagnosticReportStatus.mainSyndrome)

        val bundle = Bundle(
                id = "ignored",
                entry = listOf(
                        patient,
                        diagnosticReport,
                        personalDataConsent,
                        mainSyndrome
                ).map { BundleEntry(it) })

        return receptionService.registerPatientForBandage(bundle)
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
            byOffice.nextOfficeForPatientsInfo?.run {
                //количество в принципе разное
                assertEquals(this.size, office.extension.nextOfficeForPatientsInfo.size, "wrong number of nextOfficeForPatientsInfo for '$officeId' (${office.extension.nextOfficeForPatientsInfo}). $itemsStr")
                this.map { nextOfficeForPatientInfo ->
                    val actNextOfficeForPatientsInfo = office.extension.nextOfficeForPatientsInfo
                    val foundInAct = actNextOfficeForPatientsInfo.filter { it.subject.id!! == nextOfficeForPatientInfo.patientId }
                    assertEquals(1, foundInAct.size, "not found (or found multiple items) of $nextOfficeForPatientInfo. $itemsStr")
                    val foundItem = foundInAct.first()
                    //проверка правильности данных в найденном
                    assertEquals(nextOfficeForPatientInfo.nextOfficeId, foundItem.nextOffice.id!!, "wrong nextOfficeId of $nextOfficeForPatientInfo. $itemsStr")
                }
            }
        }
    }

    /**
     * Проверка правильности назначений определенного пациента
     * (не полная проверка назначений всех пациентов, а только в разрезе одного пациента)
     */
    fun checkServiceRequestsOfPatient(patientId: String, servReqInfos: List<ServiceRequestSimple>, desc: String? = null) {
        val actServRequests = serviceRequestService.all(patientId)
        checkServiceRequests(patientId, servReqInfos, actServRequests, desc)
    }

    /**
     * Сравнивает 2 списка назначений: [servReqInfos] ожидаемый и [actServRequests] текущий
     * пациента [patientId]
     */
    fun checkServiceRequests(patientId: String, servReqInfos: List<ServiceRequestSimple>, actServRequests: List<ServiceRequest>, desc: String? = null) {
        val descStr = desc?.let { "$it. " } ?: ""
        val servReqsStr = servReqsToString(patientId, servReqInfos, actServRequests)
        //количество в принципе разное
        assertEquals(servReqInfos.size, actServRequests.size, "${descStr}wrong number of servRequests. $servReqsStr")
        servReqInfos.forEachIndexed { index, servReqInfo ->
            val foundInAct = actServRequests.filter { it.code.code() == servReqInfo.code }
            assertEquals(1, foundInAct.size, "${descStr}not found (or found multiple items) with code '${servReqInfo.code}' of $servReqInfo. $servReqsStr")
            val foundItem = foundInAct.first()
            //проверка правильности данных в найденном
            assertEquals(patientId, foundItem.subject?.id, "${descStr}wrong patientId of $servReqInfo. $servReqsStr")
            assertEquals(servReqInfo.status, foundItem.status, "${descStr}wrong status of $servReqInfo. $servReqsStr")
            servReqInfo.locationId?.run { assertEquals(servReqInfo.locationId, foundItem.locationReference?.first()?.id, "${descStr}wrong locationId of $servReqInfo. $servReqsStr") }
            assertEquals(index, foundItem.extension?.executionOrder, "${descStr}wrong executionOrder of $servReqInfo. $servReqsStr")
        }
    }

    private fun itemsToStr(itemsByOffices: List<QueueOfOfficeSimple>, actQueueItems: List<QueueItem>): String {
        val actByOffices = actQueueItems.groupBy { it.location.id!! }
        val offices = officeService.all().filter { it.id in actByOffices.map { it.key } || it.extension.nextOfficeForPatientsInfo.isNotEmpty() }
        return "\n\nexp queue:\n" +
                itemsByOffices.joinToString("\n") { byOffice ->
                    byOffice.officeId + ":\n  " + byOffice.items.mapIndexed { index, queueItemInfo ->
                        "$index. $queueItemInfo"
                    }.joinToString("\n  "
                    )
                } +
                "\n\nactual queue:\n" +
                offices.map { office ->
                    val nextOfficeForPatientsInfoStr = if (office.extension.nextOfficeForPatientsInfo.isNotEmpty()) {
                        "  nextOfficeForPatientsInfo:\n   " + office.extension.nextOfficeForPatientsInfo.joinToString("\n    ")
                    } else ""
                    val items = actByOffices[office.id]
                    office.id + "\n  " +
                            (items?.sortedBy { it.onum }?.joinToString("\n  ") ?: "") + nextOfficeForPatientsInfoStr
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


    fun checkObsDuration(patientId: String, exp: List<ObservationDurationSimple>) {
        val act = reportService.observationHistoryOfPatient(patientId)
        val obsDurationStr = obsDurationToString(patientId, exp, act)
        //количество в принципе разное
        assertEquals(exp.size, act.size, "wrong number of observation durations. $obsDurationStr")
        exp.forEachIndexed { index, obsDurationInfo ->
            val foundItem = act[index]
            //проверка правильности данных в найденном
            assertEquals(patientId, foundItem.patientId, "wrong patientId of ${foundItem.fireDate.toStringFmtWithSeconds()}. $obsDurationStr")
            assertEquals(obsDurationInfo.code, foundItem.code, "wrong code of ${foundItem.fireDate.toStringFmtWithSeconds()}. $obsDurationStr")
            val durationDiffTiny = abs(obsDurationInfo.duration - foundItem.duration) / obsDurationInfo.duration < 0.02
            assertTrue(durationDiffTiny, "huge duration difference of ${foundItem.fireDate.toStringFmtWithSeconds()}. $obsDurationStr")
        }
    }

    private fun obsDurationToString(patientId: String, exp: List<ObservationDurationSimple>, act: List<ObservationDuration>): Any {
        return "\n\nfor patient '$patientId'\nexp durations:\n  " +
                exp.joinToString("\n  ") { it.toString() } +
                "\n\nactual:\n  " +
                act.joinToString("\n  ") { "code: " + it.code + ", duration: " + it.duration + ", patientId: " + it.patientId + ", fireDate: " + it.fireDate.toStringFmtWithSeconds() } +
                "\n\n"
    }

    fun checkQueueHistoryOfPatient(patientId: String, exp: List<QueueHistoryOfPatientSimple>) {
        val act = reportService.queueHistoryOfPatient(patientId)
        val obsDurationStr = queueHistoryOfPatientToString(patientId, exp, act)
        //количество в принципе разное
        assertEquals(exp.size, act.size, "wrong number of queue history of patient. $obsDurationStr")
        exp.forEachIndexed { index, obsDurationInfo ->
            val foundItem = act[index]
            //проверка правильности данных в найденном
            assertEquals(patientId, foundItem.patientId, "wrong patientId of ${foundItem.fireDate.toStringFmtWithSeconds()}. $obsDurationStr")
            assertEquals(obsDurationInfo.status.name, foundItem.status, "wrong status of ${foundItem.fireDate.toStringFmtWithSeconds()}. $obsDurationStr")
            assertEquals(obsDurationInfo.duration, foundItem.duration, "wrong duration of ${foundItem.fireDate.toStringFmtWithSeconds()} (${foundItem.status}). $obsDurationStr")
            assertEquals(obsDurationInfo.officeId, foundItem.officeId, "wrong officeId of ${foundItem.fireDate.toStringFmtWithSeconds()} (${foundItem.status}). $obsDurationStr")
        }
    }

    private fun queueHistoryOfPatientToString(patientId: String, exp: List<QueueHistoryOfPatientSimple>, act: List<QueueStatusDuration>): Any {
        return "\n\nfor patient '$patientId'\nexp durations:\n  " +
                exp.joinToString("\n  ") { it.toString() } +
                "\n\nactual:\n  " +
                act.joinToString("\n  ") { "status: " + it.status + ", officeId: " + it.officeId + ", duration: " + it.duration + ", fireDate: " + it.fireDate.toStringFmtWithSeconds() + ", patientId: " + it.patientId } +
                "\n\n"
    }

    private fun createQueueCode(severity: Severity) = severity.display.substring(0, 1) + "00" + counter++

    fun compareListOfString(expList: List<String>, actList: List<String>, desc: String) = Assertions.assertLinesMatch(expList.sorted(), actList.sorted())
}