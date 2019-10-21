package ru.viscur.dh.integration.mis.rest

import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.*
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
        val serviceRequestPredictor = ServiceRequestPredictor()
        val responsibleSpecialistPredictor = ResponsibleSpecialistPredictor()
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
     * @param reference [Reference] Ссылка на код МКБ-10, должна содержать поле id
     */
    @PostMapping("/serviceRequests")
    fun predictServiceRequests(@RequestBody reference: Reference): Bundle {
        val conceptId = reference.id ?: throw HttpClientErrorException(HttpStatus.BAD_REQUEST)
        val services = serviceRequestPredictor.predict(conceptId)
        val specialists = responsibleSpecialistPredictor.predict(conceptId)
        return Bundle(type = BundleType.BATCH.value, entry = (services + specialists).map { BundleEntry(it) })
    }

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