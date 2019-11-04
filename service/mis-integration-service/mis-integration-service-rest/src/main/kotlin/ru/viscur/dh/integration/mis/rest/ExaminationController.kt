package ru.viscur.dh.integration.mis.rest

import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.integration.mis.rest.api.ExaminationService

/**
 * Контроллер для осмотра пациентов ответственным врачом
 */
@RestController
@RequestMapping("/examination")
@Validated
class ExaminationController(
        private val patientService: PatientService,
        private val examinationService: ExaminationService,
        private val serviceRequestService: ServiceRequestService
) {
    /**
     * Получить список активных пациентов ответсвенного врача
     */
    @GetMapping("/patients")
    fun activeByPractitioner(@RequestParam practitionerId: String? = null) =
            mapOf("patients" to patientService.patientsToExamine(practitionerId))

    /**
     * Назначить дообследование пациенту
     */
    @PostMapping("/serviceRequests")
    fun addServiceRequests(@RequestBody bundle: Bundle) = examinationService.addServiceRequests(bundle)

    /**
     * Завершить осмотр пациента
     */
    @PostMapping
    fun completeExamination(@RequestBody bundle: Bundle) = examinationService.completeExamination(bundle)

    /**
     * Все назначения пациента
     */
    @GetMapping("/serviceRequests")
    fun serviceRequests(@RequestParam patientId: String) = Bundle(entry = serviceRequestService.all(patientId).map { BundleEntry(it) })

    /**
     * Отменить обращение пациента
     */
    @GetMapping("/cancel")
    fun cancel(@RequestParam patientId: String) {
        examinationService.cancelClinicalImpression(patientId)
    }
}