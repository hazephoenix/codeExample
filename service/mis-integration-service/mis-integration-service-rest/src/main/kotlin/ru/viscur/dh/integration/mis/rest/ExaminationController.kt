package ru.viscur.dh.integration.mis.rest

import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.integration.mis.rest.api.ExaminationService

/**
 * Контроллер для осмотра пациентов ответственным врачом
 */
@RestController
@RequestMapping("/examination")
@Validated
class ExaminationController(
        private val patientService: PatientService,
        private val examinationService: ExaminationService
) {
    /**
     * Получить список активных пациентов ответсвенного врача
     */
    @GetMapping("/patients")
    fun activeByPractitioner(@RequestParam practitionerId: String) =
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
}