package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.*
import ru.viscur.dh.queue.api.*

/**
 * Контроллер для обработки запросов подсистемы "АРМ Фельдшер"
 */
@RestController
@RequestMapping("/reception")
class ReceptionController(
        private val patientService: PatientService,
        private val queueManagerService: QueueManagerService
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
     * Определение предположительного списка услуг для маршрутного листа по диагнозу МКБ
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
     * Сохранение всех данных, полученных на АРМ Фельдшер
     */
    @PostMapping("/patient")
    fun savePatientData(@RequestBody bundle: Bundle): Bundle {
        val patientId = patientService.saveFinalPatientData(bundle)
        val serviceRequests = queueManagerService.registerPatient(patientId)
        queueManagerService.loqAndValidate()//todo del after
        return Bundle(
                entry = serviceRequests.map { BundleEntry(it) }
        )
    }
}

/**
 * Тело запроса для определения услуг в маршрутном листе и др.
 */
class ServiceRequestPredictBody(
        val diagnosis: String,
        val complaints: List<String>,
        val gender: String
)
