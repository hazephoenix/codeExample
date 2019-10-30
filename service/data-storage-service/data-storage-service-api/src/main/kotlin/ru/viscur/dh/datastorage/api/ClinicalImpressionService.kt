package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

/**
 * Сервис для работы с обращениями пациентов
 */
interface ClinicalImpressionService {
    /**
     * Активное обращение пациента
     */
    fun active(patientId: String): ClinicalImpression?

    /**
     * Отменить активное обращение, если таковое имеется.
     * Т к при создании нового может быть по ошибке 2 активных.
     * Поэтому перед созданием нового нужно отменить предыдущее
     * Отменяются также активный маршрутный лист [CarePlan], его непройденные назначения [ServiceRequest], незавершенные результаты назначений [Observation]
     */
    fun cancelActive(patientId: String)

    /**
     * Завершить активное обращение пациента
     */
    fun finish(bundle: Bundle): ClinicalImpression
}