package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import java.util.*

/**
 * Сервис для работы с обследованиями
 */
interface ObservationService {

    /**
     * Выполненных по назначению в маршр. листе в заданный период (по дате создания, при update (внесении результатов) дата не меняется)
     * не включаются те, которые были сделаны при регистрации (вес, рост и т.д.)
     */
    fun byPeriod(start: Date, end: Date): List<Observation>

    /**
     * По пациенту и статусу
     */
    fun byPatientAndStatus(patientId: String, status: ObservationStatus? = null): List<Observation>

    /**
     * По назначению-основанию (basedOn - ServiceRequest)
     */
    fun byBaseOnServiceRequestId(id: String): Observation?

    /**
     * Обследование началось
     */
    fun start(serviceRequestId: String)

    /**
     * Создать запись об обследовании
     */
    fun create(patientId: String, observation: Observation, diagnosis: String?, severity: Severity): Observation

    /**
     * Обновить запись об исследовании
     */
    fun update(patientId: String, observation: Observation): Observation

    /**
     * Отменить по назначениям в кабинете
     */
    fun cancelByServiceRequests(patientId: String, officeId: String)

    /**
     * Отменить по id назначения
     */
    fun cancelByBaseOnServiceRequestId(id: String)
}