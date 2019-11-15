package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.enums.LocationStatus

/**
 * Сервис для работы с кабинетами
 */
interface OfficeService {

    /**
     * Все кабинеты
     */
    fun all(): List<Location>

    /**
     * Изменение статуса кабинета [office] на [newStatus]
     * @param patientIdOfPrevProcess id пациента закончившегося процесса
     */
    fun changeStatus(officeId: String, newStatus: LocationStatus)

    /**
     * Добавление пациента в очередь в кабинет
     * @param asFirst принять как первого в очереди - на первую позицию. Иначе в зависимости от степени тяжести
     */
    fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int, asFirst: Boolean = false)

    /**
     * Id первого пациента в очереди в кабинета
     */
    fun firstPatientIdInQueue(officeId: String): String?

    /**
     * Удаление первого пациента из очереди в кабинет
     */
    fun deleteFirstPatientFromQueue(officeId: String)

    /**
     * Удаление пациента из очереди
     */
    fun deletePatientFromQueue(officeId: String, patientId: String)

    /**
     * Удаление пациента из информации о последнем пациенте (если он фигурирует в такой информации в каком-либо кабинете)
     */
    fun deletePatientFromLastPatientInfo(patientId: String)

    /**
     * Обновление информации о последнем пациенте у кабинета
     */
    fun updateLastPatientInfo(officeId: String, patientId: String, nextOfficeId: String?)
}