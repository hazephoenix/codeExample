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
import ru.viscur.dh.fhir.model.type.LocationExtensionNextOfficeForPatientInfo
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.queue.api.LocationMonitorInformService
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY
import ru.viscur.dh.transaction.desc.config.annotation.Tx

@Service
class OfficeServiceImpl(
        private val locationService: LocationService,
        private val patientService: PatientService,
        private val resourceService: ResourceService,
        private val queueService: QueueService,
        private val locationMonitorInformService: LocationMonitorInformService
) : OfficeService {

    override fun all() = resourceService.all(ResourceType.Location, RequestBodyForResources(
            filter = mapOf("id" to "Office:"),
            filterLike = true
    ))

    @Tx
    override fun changeStatus(officeId: String, newStatus: LocationStatus) {
        val now = now()
        val office = locationService.byId(officeId)
        val queueHistoryOfOffice = QueueHistoryOfOffice(
                location = Reference(office),
                status = office.status,
                fireDate = office.extension.statusUpdatedAt,
                duration = durationInSeconds(office.extension.statusUpdatedAt, now)
        )
        resourceService.create(queueHistoryOfOffice)
        resourceService.update(ResourceType.Location, officeId) {
            status = newStatus
            extension = extension.apply { statusUpdatedAt = now }
        }
        locationMonitorInformService.queueChanged(listOf(officeId))
    }

    @Tx
    override fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int, toIndex: Int?) {
        val queueItem = QueueItem(
                subject = referenceToPatient(id = patientId),
                location = referenceToLocation(id = officeId),
                estDuration = estDuration,
                queueNumber = patientService.queueNumber(patientId)
        )
        val queue = queueService.queueItemsOfOffice(officeId)
        if (toIndex != null) {
            queue.add(queue.indexOfLast { it.patientQueueStatus in listOf(PatientQueueStatus.GOING_TO_OBSERVATION, PatientQueueStatus.ON_OBSERVATION) } + 1 + toIndex, queueItem)
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

    @Tx
    override fun firstPatientIdInQueue(officeId: String): String? =
            queueService.queueItemsOfOffice(officeId).filter { it.patientQueueStatus == PatientQueueStatus.IN_QUEUE }.firstOrNull()?.subject?.id

    @Tx
    override fun deleteFirstPatientFromQueue(officeId: String) {
        val queue = queueService.queueItemsOfOffice(officeId)
        if (queue.isEmpty()) return
        queue.removeAt(0)
        saveQueue(officeId, queue)
    }

    @Tx
    override fun deletePatientFromQueue(officeId: String, patientId: String) {
        val queue = queueService.queueItemsOfOffice(officeId)
        queue.removeAt(queue.indexOfFirst { it.subject.id == patientId })
        saveQueue(officeId, queue)
    }

    @Tx
    override fun deletePatientFromNextOfficesForPatientsInfo(patientId: String) {
        locationService.withPatientInNextOfficeForPatientsInfo(patientId).forEach {
            val officeId = it.id
            resourceService.update(ResourceType.Location, officeId) {
                extension.nextOfficeForPatientsInfo = extension.nextOfficeForPatientsInfo.filterNot { it.subject.id!! == patientId }
            }
            locationMonitorInformService.queueChanged(listOf(officeId))
        }
    }

    @Tx
    override fun addToNextOfficeForPatientsInfo(officeId: String, patientId: String, nextOfficeId: String) {
        resourceService.update(ResourceType.Location, officeId) {
            val newNextOfficeForPatientInfo = LocationExtensionNextOfficeForPatientInfo(
                    subject = referenceToPatient(patientId),
                    severity = patientService.severity(patientId),
                    queueNumber = patientService.queueNumber(patientId),
                    nextOffice = referenceToLocation(nextOfficeId)
            )
            extension.nextOfficeForPatientsInfo = extension.nextOfficeForPatientsInfo + newNextOfficeForPatientInfo
        }
        locationMonitorInformService.queueChanged(listOf(officeId))
    }

    @Tx
    private fun saveQueue(officeId: String, queue: MutableList<QueueItem>) {
        queueService.deleteQueueItemsOfOffice(officeId)
        queue.forEachIndexed { index, it -> resourceService.create(it.apply { onum = index }) }
    }
}