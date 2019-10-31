package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ClinicalImpressionService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ServiceRequestService
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.queue.api.ExaminationService
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.CarePlan
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Created at 31.10.2019 18:03 by SherbakovaMA
 */
@Service
class ExaminationServiceImpl(
        private val patientService: PatientService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val serviceRequestService: ServiceRequestService,
        private val queueManagerService: QueueManagerService
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
        val clinicalImpression = clinicalImpressionService.completeRelated(bundle)
        val patientId = clinicalImpression.subject.id!!
        //завершить обследование в кабинете (если пациент со статусом На обследовании)
        queueManagerService.patientLeftByPatientId(patientId)
        //удалить из очереди (если пациент со статусом В очереди)
        queueManagerService.deleteFromOfficeQueue(patientId)
        return clinicalImpressionService.complete(clinicalImpression)
    }
}