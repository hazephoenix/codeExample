package ru.viscur.dh.integration.mis.rest.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.integration.mis.rest.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 01.11.2019 11:28 by SherbakovaMA
 */
@Service
class ReceptionServiceImpl(
        private val patientService: PatientService,
        private val queueManagerService: QueueManagerService
) : ReceptionService {

    @Tx
    override fun registerPatient(bundle: Bundle): List<ServiceRequest> {
        val patientId = patientService.saveFinalPatientData(bundle)
        val serviceRequests = queueManagerService.registerPatient(patientId)
        queueManagerService.loqAndValidate()//todo del after
        return serviceRequests
    }
}