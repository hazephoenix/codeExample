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
}