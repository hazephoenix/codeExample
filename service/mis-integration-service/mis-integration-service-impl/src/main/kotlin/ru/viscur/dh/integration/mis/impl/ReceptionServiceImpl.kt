package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.integration.mis.api.ReceptionService
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
        return queueManagerService.registerPatient(patientId)
    }

    @Tx
    override fun registerPatientForBandage(bundle: Bundle): List<ServiceRequest> {
        val patientId = patientService.saveFinalPatientDataForBandage(bundle)
        return queueManagerService.registerPatient(patientId)
    }
}