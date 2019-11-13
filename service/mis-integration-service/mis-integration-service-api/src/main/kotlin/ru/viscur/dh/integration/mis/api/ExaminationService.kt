package ru.viscur.dh.integration.mis.api

import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.CarePlan
import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Created at 31.10.2019 17:58 by SherbakovaMA
 *
 * Сервис для осмотра пациентов ответственным врачом
 */
interface ExaminationService {

    /**
     * Назначить дообследование пациенту
     */
    fun addServiceRequests(bundle: Bundle): CarePlan

    /**
     * Завершить осмотр пациента
     */
    fun completeExamination(bundle: Bundle): ClinicalImpression

    /**
     * Отменить обращение пациента
     */
    fun cancelClinicalImpression(patientId: String)

    /**
     * Отменить назначения пациента в кабинете
     */
    fun cancelServiceRequests(patientId: String, officeId: String)

    /**
     * Отменить назначение пациента по id назначения
     */
    fun cancelServiceRequest(id: String)

    /**
     * Изменить степень тяжести пациента
     * Переставляет в очереди не меняя кабинета
     */
    fun updateSeverity(patientId: String, severity: Severity)
}