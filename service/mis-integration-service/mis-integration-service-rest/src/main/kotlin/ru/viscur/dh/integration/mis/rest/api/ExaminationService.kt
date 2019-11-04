package ru.viscur.dh.integration.mis.rest.api

import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.CarePlan
import ru.viscur.dh.fhir.model.entity.ClinicalImpression

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
}