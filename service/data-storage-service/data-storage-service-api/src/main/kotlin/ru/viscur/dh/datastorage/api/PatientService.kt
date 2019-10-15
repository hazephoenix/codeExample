package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Created at 15.10.2019 11:51 by SherbakovaMA
 *
 * Сервис для работы с пациентами
 */
interface PatientService {

    /**
     * Пациент по [id]
     */
    fun byId(id: String): Patient?

    /**
     * Какая степень тяжести у пациента
     */
    fun severity(patientId: String): Severity
}