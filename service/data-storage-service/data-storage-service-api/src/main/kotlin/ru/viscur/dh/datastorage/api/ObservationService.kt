package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

/**
 * Сервис для работы с обследованиями
 */
interface ObservationService {
    /**
     * Создать запись об обследовании
     */
    fun create(observation: Observation): Observation?

    /**
     * Обновить запись об исследовании
     */
    fun update(observation: Observation): Observation?
}