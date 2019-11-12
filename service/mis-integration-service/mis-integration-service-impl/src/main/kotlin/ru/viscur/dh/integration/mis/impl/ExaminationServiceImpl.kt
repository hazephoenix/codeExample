package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.integration.mis.api.*
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 31.10.2019 18:03 by SherbakovaMA
 */
@Service
class ExaminationServiceImpl(
        private val patientService: PatientService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val serviceRequestService: ServiceRequestService,
        private val queueManagerService: QueueManagerService,
        private val queueService: QueueService,
        private val observationService: ObservationService,
        private val diagnosisPredictor: DiagnosisPredictor
) : ExaminationService {

    @Tx
    override fun addServiceRequests(bundle: Bundle): CarePlan {
        val patientId = bundle.entry.first { it.resource.resourceType == ResourceType.ResourceTypeId.ServiceRequest }
                .let {
                    val req = it.resource as ServiceRequest
                    patientService.byId(req.subject?.id!!).id
                }

        val carePlan = serviceRequestService.add(patientId, bundle.entry.map { it.resource as ServiceRequest })
        val prevOfficeId = queueService.isPatientInOfficeQueue(patientId)
        queueManagerService.deleteFromQueue(patientId)
        queueManagerService.calcServiceRequestExecOrders(patientId, prevOfficeId)
        queueManagerService.addToQueue(patientId, prevOfficeId)
        return carePlan
    }

    @Tx
    override fun completeExamination(bundle: Bundle): ClinicalImpression {
        //завершить обследование отв-ого
        val observation = bundle.resources(ResourceType.Observation)
                .singleOrNull()
                ?: throw Exception("Error. Not found single observation in request bundle")

        val updatedServiceRequest = serviceRequestService.updateStatusByObservation(observation)
        val patientId = updatedServiceRequest.subject?.id
                ?: throw Error("Not defined patient in subject of ServiceRequest with id: '${updatedServiceRequest.id}'")
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        val severity = patientService.severity(patientId)
        observationService.create(patientId, observation, diagnosis, severity)

        //завершить обследование в кабинете (если пациент со статусом На обследовании)
        queueManagerService.patientLeftByPatientId(patientId)
        //удалить из очереди (если пациент со статусом В очереди)
        queueManagerService.deleteFromQueue(patientId)

        // сохранить данные для предположения диагноза перед завершением обращения
        val diagnosticReport = bundle.resources(ResourceType.DiagnosticReport).firstOrNull()
                ?: throw Error("No DiagnosticReport provided")
        diagnosisPredictor.saveTrainingSample(diagnosticReport)
        //завершить обращение и связанное
        val clinicalImpression = clinicalImpressionService.completeRelated(patientId, bundle)
        return clinicalImpressionService.complete(clinicalImpression)
    }

    @Tx
    override fun cancelClinicalImpression(patientId: String) {
        queueManagerService.deleteFromQueue(patientId)
        clinicalImpressionService.cancelActive(patientId)
    }

    @Tx
    override fun cancelServiceRequests(patientId: String, officeId: String) {
        observationService.cancelByServiceRequests(patientId, officeId)
        val cancelledServiceRequests = serviceRequestService.cancelServiceRequests(patientId, officeId)
        if (cancelledServiceRequests.isNotEmpty()) {
            queueManagerService.rebasePatientIfNeeded(patientId, officeId)
        }
    }

    @Tx
    override fun cancelServiceRequest(id: String) {
        val cancelledServiceRequest = serviceRequestService.cancelServiceRequest(id)
        observationService.cancelByBaseOnServiceRequestId(id)
        val officeId = cancelledServiceRequest.locationReference?.first()?.id
        officeId?.run {
            queueManagerService.rebasePatientIfNeeded(cancelledServiceRequest.subject!!.id!!, officeId)
        }
    }

    @Tx
    override fun updateSeverity(patientId: String, severity: Severity) {
        val updated = patientService.updateSeverity(patientId, severity)
        if (updated) {
            val officeId = queueService.isPatientInOfficeQueue(patientId)
            queueManagerService.deleteFromQueue(patientId)
            officeId?.run { queueManagerService.addToOfficeQueue(patientId, officeId) }
        }
    }
}