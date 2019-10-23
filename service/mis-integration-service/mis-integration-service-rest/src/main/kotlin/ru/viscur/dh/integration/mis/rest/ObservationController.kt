package ru.viscur.dh.integration.mis.rest

import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.integration.mis.rest.config.annotation.*

/**
 * Контроллер для работы с обследованиями
 */
@RestController
@RequestMapping("/Observation")
@Validated
class ObservationController(
        private val observationService: ObservationService
) {
    /**
     * Получить обследование по статусу и id пациента
     */
    @GetMapping
    fun getByPatientAndStatus(@RequestParam patientId: String, @RequestParam status: ObservationStatus) =
        observationService.findByPatientAndStatus(patientId, status)

    /**
     * Создать обследование
     */
    @PostMapping
    fun create(@RequestBody observation: Observation) = observationService.create(observation)

    /**
     * Обновить обследование
     */
    @PutMapping
    fun update(@RequestBody @Exists observation: Observation) = observationService.update(observation)
}