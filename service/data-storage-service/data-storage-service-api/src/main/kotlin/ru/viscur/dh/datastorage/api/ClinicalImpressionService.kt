package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Сервис для работы с обращениями пациентов
 */
interface ClinicalImpressionService {

    /**
     * Все активные обращения пациентов
     */
    fun allActive(): List<ClinicalImpression>

    /**
     * Есть ли активное обращение
     * Если есть, то возвращается найденное
     */
    fun hasActive(patientId: String): ClinicalImpression?

    /**
     * Активное обращение пациента
     * Падение, если не найдено
     */
    fun active(patientId: String): ClinicalImpression

    /**
     * По id назначения
     */
    fun byServiceRequest(serviceRequestId: String): ClinicalImpression

    /**
     * Отменить активное обращение, если таковое имеется.
     * Т к при создании нового может быть по ошибке 2 активных.
     * Поэтому перед созданием нового нужно отменить предыдущее
     * Отменяются также активный маршрутный лист [CarePlan], его непройденные назначения [ServiceRequest], незавершенные результаты назначений [Observation]
     */
    fun cancelActive(patientId: String)

    /**
     * Завершить все, что связано с активным обращением пациента
     */
    fun completeRelated(patientId: String, bundle: Bundle): ClinicalImpression

    /**
     * Завершить активное обращение пациента
     */
    fun complete(clinicalImpression: ClinicalImpression): ClinicalImpression
}