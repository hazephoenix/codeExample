package ru.viscur.dh.integration.mis.rest

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.ObservationService
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.integration.mis.api.ObservationInCarePlanService
import ru.viscur.dh.integration.mis.rest.config.annotation.ResourceExists

/**
 * Контроллер для работы с обследованиями
 */
@RestController
@RequestMapping("/Observation")
@Validated
class ObservationController(
        private val observationsService: ObservationInCarePlanService,
        private val observationService: ObservationService
) {
    /**
     * Получить обследование по статусу и id пациента
     * Если [status] не задан, выбираются без ограничения по статусу
     */
    @GetMapping
    fun byPatientAndStatus(@RequestParam patientId: String, @RequestParam status: ObservationStatus? = null) =
            observationService.byPatientAndStatus(patientId, status)

    @GetMapping("/start")
    fun start(@RequestParam serviceRequestId: String) {
        observationService.start(serviceRequestId)
    }

    /**
     * Создать обследование
     */
    @PostMapping
    fun create(@RequestBody observation: Observation) = observationsService.create(observation)

    /**
     * Обновить обследование
     */
    @PutMapping
    fun update(@RequestBody @ResourceExists observation: Observation) = observationsService.update(observation)
}