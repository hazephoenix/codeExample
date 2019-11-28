package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.CLINICAL_IMPRESSION
import ru.viscur.dh.fhir.model.entity.CarePlan
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.CarePlanStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.isInspectionOfResp
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 22.11.2019 16:39 by SherbakovaMA
 */
@Service
class ObservationInCarePlanServiceImpl(
        private val resourceService: ResourceService,
        private val queueManagerService: QueueManagerService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val observationService: ObservationService,
        private val patientService: PatientService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val serviceRequestService: ServiceRequestService,
        private val carePlanService: CarePlanService
) : ObservationInCarePlanService {

    override fun create(observation: Observation): Observation {
        val patientId = patientIdByObservation(observation)
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        val severity = patientService.severity(patientId)
        observation.basedOn?.id?.run {
            val updatedServiceRequest = resourceService.update(ResourceType.ServiceRequest, this) {
                extension = extension?.apply { execEnd = now() }
                        ?: ServiceRequestExtension(execEnd = now())
                observation.code = this.code
            }
            if (diagnosis != null && updatedServiceRequest.extension?.execStart != null) {
                observationDurationService.saveToHistory(patientId, updatedServiceRequest.code.code(), diagnosis, severity, updatedServiceRequest.extension!!.execStart!!, updatedServiceRequest.extension!!.execEnd!!)
            }
        } ?: throw Exception("not defined serviceRequestId in basedOn of observation")
        updateRelated(patientId, observation, severity, diagnosis)
        return observationService.create(patientId, observation, diagnosis, severity)
    }

    override fun update(observation: Observation): Observation {
        val patientId = patientIdByObservation(observation)
        val updatedObservation = observationService.update(patientId, observation)
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        val severity = patientService.severity(patientId)
        updateRelated(patientId, updatedObservation, severity, diagnosis)
        return updatedObservation
    }

    private fun patientIdByObservation(observation: Observation): String = clinicalImpressionService.byServiceRequest(
            observation.basedOn?.id
                    ?: throw Exception("Error. Not defined serviceRequestId in basedOn field of Observation with id = '${observation.id}'")
    ).let {
        it.subject.id
                ?: throw Exception("Error. Not defined patientId in subject field of ClinicalImpression with id = '${it.id}'")
    }

    /**
     * Обновить связанные ресурсы
     */
    private fun updateRelated(patientId: String, observation: Observation, severity: Severity, diagnosis: String?) {
        // Обновить статус направления на обследование
        val updatedServiceRequest = serviceRequestService.updateStatusByObservation(observation)
        // Обновить статус маршрутного листа
        val updatedCarePlan = updateCarePlan(patientId, updatedServiceRequest.id)
        // Обновить обращение, если необходимо
        if (updatedCarePlan != null && updatedCarePlan.status == CarePlanStatus.results_are_ready) {
            val clinicalImpression = clinicalImpressionService.byServiceRequest(updatedServiceRequest.id)
            val forBandageOnly = clinicalImpression.extension.forBandageOnly
            if (forBandageOnly != null && forBandageOnly) {
                //завершить обследование в кабинете (если пациент со статусом На обследовании)
                queueManagerService.patientLeftByPatientId(patientId)
                //удалить из очереди (если пациент со статусом В очереди)
                queueManagerService.deleteFromQueue(patientId)
                //сохранить в историю продолжительность обработки обращения пациента
                observationDurationService.saveToHistory(patientId, CLINICAL_IMPRESSION, diagnosis, severity, clinicalImpression.date, now())
                clinicalImpressionService.complete(clinicalImpression)
            }
        }
    }

    /**
     * Обновить соответствующий направлению [ServiceRequest] маршрутный лист [CarePlan]
     */
    private fun updateCarePlan(patientId: String, serviceRequestId: String): CarePlan? {
        return carePlanService.byServiceRequestId(serviceRequestId)?.let { carePlan ->
            resourceService.update(ResourceType.CarePlan, carePlan.id) {
                val serviceRequests = serviceRequestService.all(patientId)
                val serviceRequestsWithoutResp = serviceRequests.filter { !it.isInspectionOfResp() }
                status = when {
                    serviceRequestsWithoutResp.any { it.status == ServiceRequestStatus.active } -> CarePlanStatus.active
                    serviceRequestsWithoutResp.all { it.status == ServiceRequestStatus.completed } -> CarePlanStatus.results_are_ready
                    else -> CarePlanStatus.waiting_results
                }
            }
        }
    }
}