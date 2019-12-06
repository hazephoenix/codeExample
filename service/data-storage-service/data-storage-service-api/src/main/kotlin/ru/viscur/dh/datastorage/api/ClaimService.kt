package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

/**
 * Сервис для работы с обращением [Claim] пациента
 */
interface ClaimService {
    /**
     * Получить активное обращение пациента
     */
    fun active(patientId: String): Claim?
}