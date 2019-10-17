package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*

/**
 * Контроллер для работы с обследованиями
 */
@RestController
@RequestMapping("/Observation")
class ObservationController(
        private val observationService: ObservationService
) {

    /**
     * Создать обследование
     */
    @PostMapping
    fun create(@RequestBody observation: Observation) = observationService.create(observation)

    /**
     * Обновить обследование
     */
    @PutMapping
    fun update(@RequestBody observation: Observation) = observationService.update(observation)
}