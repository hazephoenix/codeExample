package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
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
     * Все назначения в маршрутном листе, упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun serviceRequests(patientId: String): List<ServiceRequest>

    /**
     * Все непройденные назначения в маршрутном листе, упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun activeServiceRequests(patientId: String): List<ServiceRequest>

    /**
     * Все непройденные назначения в маршрутном листе в определенном кабинете,
     * упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun activeServiceRequests(patientId: String, officeId: String): List<ServiceRequest>

    fun queueStatusOfPatient(patientId: String): PatientQueueStatus

    /**
     * Код предварительного диагноза
     */
    fun preliminaryDiagnosticConclusion(patientId: String): String?

    /**
     * Определение по диагнозу МКБ, полу пациента и жалобам:
     * - предположительного списка услуг для маршрутного листа
     * - отв врача (указан в одной из услуги)
     * - списка врачей, из которых можно выбрать отв.
     * @param diagnosis код диагноза МКБ-10
     * @param gender пол пациента
     * @param complaints жалобы
     */
    fun predictServiceRequests(diagnosis: String, gender: String, complaints: List<String>): Bundle

    /**
     * Метод сохраняет конечную (заполненную полностью) информацию о пациенте
     * по окончании приема фельдшера (/reception/patient)
     *
     * @param bundle Контейнер с данными обращения
     * @return id пациента в data-storage
     */
    fun saveFinalPatientData(bundle: Bundle): String

    /**
     * Активное обращение пациента
     */
    fun activeClinicalImpression(patientId: String): ClinicalImpression?
}