package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.QueueHistoryOfPatient
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.queue.api.PatientStatusService
import ru.viscur.dh.queue.impl.msToSeconds
import ru.viscur.dh.queue.impl.now
import java.util.*

@Service
class PatientStatusServiceImpl(
        private val resourceService: ResourceService,
        private val patientService: PatientService
) : PatientStatusService {

    override fun changeStatus(patientId: String, newStatus: PatientQueueStatus, officeIdOfPrevProcess: String?, saveCurrentStatusToHistory: Boolean) {
        val now = now()
        if (saveCurrentStatusToHistory) {
            saveCurrentStatus(patientId, officeIdOfPrevProcess, now)
        }
        resourceService.update(ResourceType.Patient, patientId) {
            extension.apply {
                queueStatus = newStatus
                queueStatusUpdatedAt = now
            }
        }
    }

    override fun saveCurrentStatus(patientId: String, officeIdOfPrevProcess: String?, now: Date) {
        val patient = patientService.byId(patientId)
        val queueStatusUpdatedAt = patient.extension.queueStatusUpdatedAt
        val queueHistoryOfPatient = QueueHistoryOfPatient(
                subject = referenceToPatient(id = patientId),
                location = officeIdOfPrevProcess?.let { referenceToLocation(id = officeIdOfPrevProcess) },
                status = patient.extension.queueStatus!!,
                fireDate = queueStatusUpdatedAt!!,
                duration = msToSeconds(now.time - queueStatusUpdatedAt.time)
        )
        resourceService.create(queueHistoryOfPatient)
    }
}
