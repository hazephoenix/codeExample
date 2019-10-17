package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.QueueService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.QueueHistoryOfOffice
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.LocationExtension
import ru.viscur.dh.fhir.model.type.LocationExtensionLastPatientInfo
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY
import ru.viscur.dh.queue.impl.ageGroup
import ru.viscur.dh.queue.impl.msToSeconds
import ru.viscur.dh.queue.impl.now

@Service
class OfficeServiceImpl(
        private val locationService: LocationService,
        private val patientService: PatientService,
        private val resourceService: ResourceService,
        private val queueService: QueueService
) : OfficeService {

    override fun changeStatus(officeId: String, newStatus: LocationStatus, patientIdOfPrevProcess: String?) {
        val now = now()
        val office = locationService.byId(officeId)
        val queueHistoryOfOffice = QueueHistoryOfOffice(
                location = Reference(office),
                status = office.status,
                fireDate = office.extension?.statusUpdatedAt,
                duration = office.extension?.statusUpdatedAt?.let { msToSeconds(now.time - it.time) }
        )

        patientIdOfPrevProcess?.run {
            val patient = patientService.byId(this)
            queueHistoryOfOffice.apply {
                severity = patientService.severity(patientIdOfPrevProcess)
                diagnosticConclusion = patientService.preliminaryDiagnosticConclusion(patientIdOfPrevProcess)
                ageGroup = ageGroup(patient.birthDate)
            }
        }
        resourceService.create(queueHistoryOfOffice)
        resourceService.update(office.apply {
            status = newStatus
            extension = extension?.let { it.apply { statusUpdatedAt = now } }
                    ?: LocationExtension(statusUpdatedAt = now)
        })
    }

    override fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int) {
        val queueItem = QueueItem(
                subject = referenceToPatient(id = patientId),
                location = referenceToLocation(id = officeId),
                estDuration = estDuration
        )
        val queue = queueItems(officeId)
        when (val userSeverity = patientService.severity(patientId)) {
            Severity.GREEN -> queue.add(queueItem)
            else -> {
                val severities = if (userSeverity == Severity.RED) listOf(Severity.RED) else SEVERITY_WITH_PRIORITY
                if (queue.any { it.severity in severities }) {
                    queue.add(queue.indexOfLast { it.severity in severities } + 1, queueItem)
                } else {
                    if (queue.any { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE }) {
                        queue.add(queue.indexOfFirst { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE }, queueItem)
                    } else {
                        queue.add(queueItem)
                    }
                }
            }
        }
        saveQueue(officeId, queue)
    }

    override fun firstPatientInQueue(officeId: String): String? =
            queueService.queueItemsOfOffice(officeId).firstOrNull()?.subject?.id

    override fun deleteFirstPatientFromQueue(officeId: String) {
        val queue = queueItems(officeId)
        if (queue.isEmpty()) return
        queue.removeAt(0)
        saveQueue(officeId, queue)
    }

    override fun deletePatientFromQueue(officeId: String, patientId: String) {
        val queue = queueItems(officeId)
        queue.removeAt(queue.indexOfFirst { it.subject.id == patientId })
        saveQueue(officeId, queue)
    }

    override fun deletePatientFromLastPatientInfo(patientId: String) {
        locationService.withPatientInLastPatientInfo(patientId).forEach {
            it.extension?.lastPatientInfo = null
            resourceService.update(it)
        }
    }

    override fun updateLastPatientInfo(officeId: String, patientId: String, nextOfficeId: String?) {
        val office = locationService.byId(officeId)
        val newLastPatientInfo = LocationExtensionLastPatientInfo(
                subject = Reference(ResourceType.ResourceTypeId.Patient, patientId),
                nextOffice = nextOfficeId?.let { Reference(ResourceType.ResourceTypeId.Location, nextOfficeId) }
        )
        office.extension = office.extension?.apply { lastPatientInfo = newLastPatientInfo }
                ?: LocationExtension(lastPatientInfo = newLastPatientInfo)
    }

    private fun saveQueue(officeId: String, queue: MutableList<QueueItem>) {
        queueService.deleteQueueItemsOfOffice(officeId)
        queue.forEachIndexed { index, it -> resourceService.create(it.apply { onum = index }) }
    }

    private fun queueItems(officeId: String): MutableList<QueueItem> =
            queueService.queueItemsOfOffice(officeId).map { queueItem ->
                val patientId = queueItem.subject.id
                queueItem.apply {
                    severity = patientService.severity(patientId!!)
                    patientQueueStatus = patientService.byId(patientId!!).extension.queueStatus
                }
            }.toMutableList()
}