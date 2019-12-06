package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.datastorage.api.util.PatientClassifier
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.integration.mis.rest.dto.ServiceRequestPredictBody

/**
 * Контроллер для обработки запросов подсистемы "АРМ Фельдшер"
 */
@RestController
@RequestMapping("/reception")
class ReceptionController(
        private val patientService: PatientService,
        private val receptionService: ReceptionService,
        private val diagnosisPredictor: DiagnosisPredictor,
        private val patientClassifier: PatientClassifier
) {

    /**
     * Определение категории пациента
     */
    @PostMapping("/severity")
    fun getSeverity(@RequestBody bundle: Bundle, @RequestParam takeSyndromes: Int): SeverityResponse =
            patientClassifier.classify(bundle, takeSyndromes)

    /**
     * Определение предв. диагноза
     */
    @PostMapping("/diagnostic")
    fun predictDiagnosis(@RequestBody bundle: Bundle, @RequestParam take: Int) = diagnosisPredictor.predict(bundle, take)

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

    /**
     * see [ReceptionService.registerPatientForBandage]
     */
    @PostMapping("/patientForBandage")
    fun registerPatientForBandage(@RequestBody bundle: Bundle) = Bundle(
            entry = receptionService.registerPatientForBandage(bundle).map { BundleEntry(it) }
    )
}