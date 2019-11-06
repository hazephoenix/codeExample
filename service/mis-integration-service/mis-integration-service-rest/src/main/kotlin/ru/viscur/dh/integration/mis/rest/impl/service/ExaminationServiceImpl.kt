package ru.viscur.dh.integration.mis.rest.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ClinicalImpressionService
import ru.viscur.dh.datastorage.api.ObservationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ServiceRequestService
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.CarePlan
import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.resources
import ru.viscur.dh.integration.mis.rest.api.ExaminationService
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
        private val observationService: ObservationService
) : ExaminationService {

    @Tx
    override fun addServiceRequests(bundle: Bundle): CarePlan {
        val patientId = bundle.entry.first { it.resource.resourceType == ResourceType.ResourceTypeId.ServiceRequest }
                .let {
                    val req = it.resource as ServiceRequest
                    patientService.byId(req.subject?.id!!).id
                }

        val carePlan = serviceRequestService.add(patientId, bundle.entry.map { it.resource as ServiceRequest })
        queueManagerService.deleteFromOfficeQueue(patientId)
        queueManagerService.calcServiceRequestExecOrders(patientId)
        queueManagerService.addToOfficeQueue(patientId)
        return carePlan
    }

    @Tx
    override fun completeExamination(bundle: Bundle): ClinicalImpression {
        //todo возможно логичнее: завершить обследование, удалить из очереди, завершить обращение и связанное

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
        queueManagerService.deleteFromOfficeQueue(patientId)
        //завершить обращение
        val clinicalImpression = clinicalImpressionService.completeRelated(patientId, bundle)
        return clinicalImpressionService.complete(clinicalImpression)
    }

    @Tx
    override fun cancelClinicalImpression(patientId: String) {
        queueManagerService.deleteFromOfficeQueue(patientId)
        clinicalImpressionService.cancelActive(patientId)
    }
}