package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.CLINICAL_IMPRESSION
import ru.viscur.dh.datastorage.api.util.INSPECTION_TYPES
import ru.viscur.dh.fhir.model.entity.CarePlan
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.isInspectionOfResp
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.queue.api.QueueForPractitionersInformService
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
        private val carePlanService: CarePlanService,
        private val queueForPractitionersInformService: QueueForPractitionersInformService
) : ObservationInCarePlanService {

    override fun create(observation: Observation): Observation {
        val patientId = patientIdByObservation(observation)
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        val severity = patientService.severity(patientId)
        val updatedServiceRequest = observation.basedOn?.id()?.let {
            val updatedServiceRequest = resourceService.update(ResourceType.ServiceRequest, it) {
                extension = extension?.apply { execEnd = now() }
                        ?: ServiceRequestExtension(execEnd = now())
                observation.code = this.code
            }
            if (diagnosis != null && updatedServiceRequest.extension?.execStart != null) {
                observationDurationService.saveToHistory(patientId, updatedServiceRequest.code.code(), diagnosis, severity, updatedServiceRequest.extension!!.execStart!!, updatedServiceRequest.extension!!.execEnd!!)
            }
            updatedServiceRequest
        } ?: throw Exception("not defined serviceRequestId in basedOn of observation")
        updateRelated(patientId, observation, severity, diagnosis)

        val observationType = observation.code.code()
        if (observationType in INSPECTION_TYPES) {
            //проведено обсл-е отв-го, оповещаем отв.
            if (updatedServiceRequest.isInspectionOfResp()) {
                queueForPractitionersInformService.patientDeletedFromPractitionerQueue(patientId, updatedServiceRequest.performer!!.first().id())
            } else {
                //проведен осмотр не по отв., оповещаем всех заинтересованных, всех кто мог этот осмотр произвести теперь не должен видеть этого пациента
                queueForPractitionersInformService.patientWasInspected(patientId, observationType)
            }
        }

        //осталось одно обсл-е - осмотр отв-ого - оповещаем отв-ого
        if (!updatedServiceRequest.isInspectionOfResp()) {
            val activeServiceRequests = serviceRequestService.active(patientId)
            if (activeServiceRequests.isNotEmpty() && activeServiceRequests.all { it.isInspectionOfResp() }) {
                val serviceRequestOfResp = activeServiceRequests.first()
                queueForPractitionersInformService.patientAddedToPractitionerQueue(patientId, serviceRequestOfResp.performer!!.first().id(), serviceRequestOfResp.code.code())
            }
        }
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
            observation.basedOn?.id()
                    ?: throw Exception("Error. Not defined serviceRequestId in basedOn field of Observation with id = '${observation.id}'")
    ).subject.id()

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
            } else {
                if (!updatedServiceRequest.isInspectionOfResp()) {
                    queueForPractitionersInformService.resultsAreReadyInCarePlan(patientId, clinicalImpression)
                }
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