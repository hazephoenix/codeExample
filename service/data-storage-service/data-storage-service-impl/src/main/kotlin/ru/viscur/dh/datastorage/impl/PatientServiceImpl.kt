package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Created at 15.10.2019 11:52 by SherbakovaMA
 *
 * todo
 */
@Service
class PatientServiceImpl(
        private val resourceService: ResourceService
) : PatientService {

    override fun byId(id: String): Patient? = resourceService.byId(ResourceType.Patient, id)

    override fun severity(patientId: String): Severity {
//        todo resourceService.all(ResourceType.QuestionnaireResponse)
        return Severity.RED
    }
}