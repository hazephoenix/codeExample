package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.queue.api.QueueManagerService

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
        val serviceRequestPredictor = ServiceRequestPredictor()
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
     */
    @PostMapping("/serviceRequests")
    fun predictServiceRequests(@RequestBody listResource: ListResource) = serviceRequestPredictor.predict(listResource)

    /**
     * Сохранение всех данных, полученных на АРМ Фельдшер
     */
    @PostMapping("/patient")
    fun savePatientData(@RequestBody bundle: Bundle): Bundle {
        // TODO: fix dialect error
        val patientId = patientService.saveFinalPatientData(bundle)
        val serviceRequests = queueManagerService.registerPatient(patientId)
        return Bundle(
                entry = serviceRequests.map { BundleEntry(it) }
        )
    }
}