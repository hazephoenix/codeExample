package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

/**
 * Created at 15.10.2019 11:51 by SherbakovaMA
 *
 * Сервис для работы с пациентами
 */
interface PatientService {

    /**
     * Пациент по [id]
     */
    fun byId(id: String): Patient

    /**
     * Пациент ЕНП [IdentifierType.ENP][ru.viscur.dh.fhir.model.valueSets.IdentifierType.ENP]
     * @param value значение ЕНП
     */
    fun byEnp(value: String): Patient?

    /**
     * Какая степень тяжести у пациента
     */
    fun severity(patientId: String): Severity

    /**
     * Узнать статус пациента в очереди [PatientQueueStatus]
     */
    fun queueStatusOfPatient(patientId: String): PatientQueueStatus

    /**
     * Код предварительного диагноза
     */
    fun preliminaryDiagnosticConclusion(patientId: String): String?

    /**
     * Метод сохраняет конечную (заполненную полностью) информацию о пациенте
     * по окончании приема фельдшера (/reception/patient)
     *
     * @param bundle Контейнер с данными обращения
     * @return id пациента в data-storage
     */
    fun saveFinalPatientData(bundle: Bundle): String
}