package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.impl.*
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*

/**
 * Контроллер для обработки запросов подсистемы "АРМ Фельдшер"
 */
@RestController
@RequestMapping("/reception")
class ReceptionController(
        private val patientServiceImpl: PatientServiceImpl
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
        val patientId = patientServiceImpl.saveFinalPatientData(bundle)
        val serviceRequests = patientServiceImpl.serviceRequests(patientId)
        return Bundle(
                entry = serviceRequests.map { BundleEntry(it) }
        )
    }
}