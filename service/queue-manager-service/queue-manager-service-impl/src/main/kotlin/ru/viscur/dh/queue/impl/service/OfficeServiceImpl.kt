package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.*
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
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
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY
import ru.viscur.dh.queue.impl.ageGroup

@Service
class OfficeServiceImpl(
        private val locationService: LocationService,
        private val patientService: PatientService,
        private val resourceService: ResourceService,
        private val queueService: QueueService
) : OfficeService {

    override fun all() = resourceService.all(ResourceType.Location, RequestBodyForResources(
            filter = mapOf("id" to "Office:"),
            filterLike = true
    ))

    override fun changeStatus(officeId: String, newStatus: LocationStatus, patientIdOfPrevProcess: String?) {
        val now = now()
        val office = locationService.byId(officeId)
        val queueHistoryOfOffice = QueueHistoryOfOffice(
                location = Reference(office),
                status = office.status,
                fireDate = office.extension?.statusUpdatedAt,
                duration = office.extension?.statusUpdatedAt?.let { durationInSeconds(it, now) }
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
        resourceService.update(ResourceType.Location, officeId) {
            status = newStatus
            extension = extension?.apply { statusUpdatedAt = now }
                    ?: LocationExtension(statusUpdatedAt = now)
        }
    }

    override fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int, asFirst: Boolean) {
        val queueItem = QueueItem(
                subject = referenceToPatient(id = patientId),
                location = referenceToLocation(id = officeId),
                estDuration = estDuration
        )
        val queue = queueService.queueItemsOfOffice(officeId)
        if (asFirst) {
            queue.add(queue.indexOfLast { it.patientQueueStatus in listOf(PatientQueueStatus.GOING_TO_OBSERVATION, PatientQueueStatus.ON_OBSERVATION) } + 1, queueItem)
        } else {
            when (val userSeverity = patientService.severity(patientId)) {
                Severity.GREEN -> queue.add(queueItem)
                else -> {
                    //степени тяжести, которые тек. пациент должен пропустить
                    val severitiesShouldBeBefore = if (userSeverity == Severity.RED) listOf(Severity.RED) else SEVERITY_WITH_PRIORITY
                    //если есть те которые пропускает - ставим после них
                    if (queue.any { it.severity in severitiesShouldBeBefore }) {
                        queue.add(queue.indexOfLast { it.severity in severitiesShouldBeBefore } + 1, queueItem)
                        //иначе ставим перед всеми кто в очереди
                    } else {
                        if (queue.any { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE }) {
                            queue.add(queue.indexOfFirst { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE }, queueItem)
                        } else {
                            //очереди нет: либо список пуст, либо там все GOING_TO_OBSERVATION/ON_OBSERVATION
                            queue.add(queueItem)
                        }
                    }
                }
            }
        }
        saveQueue(officeId, queue)
    }

    override fun firstPatientIdInQueue(officeId: String): String? =
            queueService.queueItemsOfOffice(officeId).filter { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE }.firstOrNull()?.subject?.id

    override fun deleteFirstPatientFromQueue(officeId: String) {
        val queue = queueService.queueItemsOfOffice(officeId)
        if (queue.isEmpty()) return
        queue.removeAt(0)
        saveQueue(officeId, queue)
    }

    override fun deletePatientFromQueue(officeId: String, patientId: String) {
        val queue = queueService.queueItemsOfOffice(officeId)
        queue.removeAt(queue.indexOfFirst { it.subject.id == patientId })
        saveQueue(officeId, queue)
    }

    override fun deletePatientFromLastPatientInfo(patientId: String) {
        locationService.withPatientInLastPatientInfo(patientId).forEach {
            resourceService.update(ResourceType.Location, it.id) {
                if (extension?.lastPatientInfo?.subject?.id == patientId) {
                    extension?.lastPatientInfo = null
                }
            }
        }
    }

    override fun updateLastPatientInfo(officeId: String, patientId: String, nextOfficeId: String?) {
        resourceService.update(ResourceType.Location, officeId) {
            val newLastPatientInfo = LocationExtensionLastPatientInfo(
                    subject = referenceToPatient(patientId),
                    nextOffice = nextOfficeId?.let { referenceToLocation(nextOfficeId) }
            )
            extension = extension?.apply { lastPatientInfo = newLastPatientInfo }
                    ?: LocationExtension(lastPatientInfo = newLastPatientInfo)
        }
    }

    private fun saveQueue(officeId: String, queue: MutableList<QueueItem>) {
        queueService.deleteQueueItemsOfOffice(officeId)
        queue.forEachIndexed { index, it -> resourceService.create(it.apply { onum = index }) }
    }
}