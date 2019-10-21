package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

/**
 * Сервис для работы с обследованиями
 */
interface ObservationService {
    fun findByPatient(patientId: String, status: ObservationStatus): List<Observation?>

    /**
     * Создать запись об обследовании
     */
    fun create(observation: Observation): Observation?

    /**
     * Обновить запись об исследовании
     */
    fun update(observation: Observation): Observation?
}