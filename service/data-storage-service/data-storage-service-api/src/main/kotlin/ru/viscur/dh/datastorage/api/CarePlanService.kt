package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

/**
 * Сервис для работы с маршрутными листами
 */
interface CarePlanService {
    /**
     * Получить текущий маршрутный лист пациента
     */
    fun current(patientId: String): CarePlan?

    /**
     * Получить [CarePlan] по id [ServiceRequest]
     */
    fun byServiceRequestId(serviceRequestId: String): CarePlan?

    fun complete(carePlanId: String)
}