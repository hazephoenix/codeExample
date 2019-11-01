package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

/**
 * Сервис для работы с обследованиями
 */
interface ObservationService {

    /**
     * По пациенту и статусу
     */
    fun findByPatientAndStatus(patientId: String, status: ObservationStatus): List<Observation?>

    /**
     * По назначению-основанию (basedOn - ServiceRequest)
     */
    fun byBaseOnServiceRequestId(id: String): Observation?

    /**
     * Создать запись об обследовании
     */
    fun create(patientId: String, observation: Observation): Observation

    /**
     * Обновить запись об исследовании
     */
    fun update(patientId: String, observation: Observation): Observation
}