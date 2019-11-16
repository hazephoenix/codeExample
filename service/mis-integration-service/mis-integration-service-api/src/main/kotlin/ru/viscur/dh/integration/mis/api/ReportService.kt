package ru.viscur.dh.integration.mis.api

import ru.viscur.dh.fhir.model.dto.ObservationDuration
import ru.viscur.dh.fhir.model.dto.QueueStatusDuration
import ru.viscur.dh.integration.mis.api.dto.*
import java.util.*

/**
 * Created at 12.11.2019 9:43 by SherbakovaMA
 *
 * Сервис для отчетов
 */
interface ReportService {

    /**
     * Информация о продолжительности проведения пациента в этапах очереди за последние сутки
     */
    fun queueHistoryOfPatient(patientId: String): List<QueueStatusDuration>

    /**
     * Информация о продолжительности проведения услуг пациенту за последние сутки
     */
    fun observationHistoryOfPatient(patientId: String): List<ObservationDuration>

    /**
     * Информация об очередях в кабинеты на тек. момент
     */
    fun queueInOffices(withPractitioners: Boolean = false): List<QueueInOfficeDto>

    /**
     * Информация об очереди в кабинет на тек. момент
     */
    fun queueInOffice(officeId: String): List<QueueInOfficeDto>

    /**
     * Информация об очереди для врача на тек. момент
     * Определяется в каком кабинете находится врач и определяется по [queueInOffice]
     */
    fun queueOfPractitioner(practitionerId: String): List<QueueInOfficeDto>

    /**
     * Текущая нагрузка на врачей
     */
    fun workload(): List<QueueInOfficeDto> = queueInOffices(true)

    /**
     * История нагрузки на врачей за период
     */
    fun workloadHistory(start: Date, end: Date): List<WorkloadItemDto>

    /**
     * Информация об очередях в кабинет за период
     */
    fun queueHistory(start: Date, end: Date): List<QueueInOfficeHistoryDto>
}