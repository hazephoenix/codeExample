package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.integration.mis.rest.dto.ServiceRequestPredictBody

/**
 * Контроллер для обработки запросов подсистемы "АРМ Фельдшер"
 */
@RestController
@RequestMapping("/reception")
class ReceptionController(
        private val patientService: PatientService,
        private val receptionService: ReceptionService
) {
    companion object {
        val patientClassifier = PatientClassifier()
        val diagnosisPredictor = DiagnosisPredictor()
    }

    /**
     * Определение категории пациента
     */
    @PostMapping("/severity")
    fun getSeverity(@RequestBody bundle: Bundle): SeverityResponse = patientClassifier.classify(bundle)

    /**
     * Определение предв. диагноза
     */
    @PostMapping("/diagnostic")
    fun predictDiagnosis(@RequestBody bundle: Bundle) = diagnosisPredictor.predict()

    /**
     * Определение предположительного списка услуг для маршрутного листа по диагнозу МКБ TODO
     *
     * @param concept [Concept] код МКБ-10
     */
    @PostMapping("/serviceRequests")
    fun predictServiceRequests(@RequestBody body: ServiceRequestPredictBody) =
            patientService.predictServiceRequests(
                    body.diagnosis,
                    body.gender,
                    body.complaints
            )

    /**
     * see [ReceptionService.registerPatient]
     */
    @PostMapping("/patient")
    fun registerPatient(@RequestBody bundle: Bundle) = Bundle(
            entry = receptionService.registerPatient(bundle).map { BundleEntry(it) }
    )
}