package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

/**
 * Сервис для работы с маршрутными листами
 */
interface CarePlanService {
    /**
     * Получить активный маршрутный лист пациента
     */
    fun active(patientId: String): CarePlan?
    /**
     * Получить список активных маршрутных листов по id ответственного врача
     */
    fun activeByPractitioner(practitionerId: String): List<CarePlan>
}