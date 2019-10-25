package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

/**
 * Сервис для работы с маршрутными листами
 */
interface CarePlanService {
    /**
     * Получить активный маршрутный лист пациента
     */
    fun getActive(patientId: String): CarePlan?
    /**
     * Получить список активных маршрутных листов по id ответственного врача
     */
    fun getActiveByPractitioner(practitionerId: String): List<CarePlan>
}