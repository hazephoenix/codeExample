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
     * @param toIndex в какую позицию поставить (нумерация с 0, если первыйм = 0). Если не задано, то в зависимости от степени тяжести
     */
    fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int, toIndex: Int? = null)

    /**
     * Id первого пациента в очереди в кабинета
     */
    fun firstPatientIdInQueue(officeId: String): String?

    /**
     * Удаление пациента из очереди
     */
    fun deletePatientFromQueue(officeId: String, patientId: String)

    /**
     * Удаление пациента из информации о последующем кабинете пациента (если он фигурирует в такой информации в каком-либо кабинете)
     */
    fun deletePatientFromNextOfficesForPatientsInfo(patientId: String)

    /**
     * Добавление в информацию о последующих кабинетов для пациентов у кабинета
     */
    fun addToNextOfficeForPatientsInfo(officeId: String, patientId: String, nextOfficeId: String)
}