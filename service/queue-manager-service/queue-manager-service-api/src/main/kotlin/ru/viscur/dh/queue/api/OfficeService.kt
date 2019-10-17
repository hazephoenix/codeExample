package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.enums.LocationStatus

interface OfficeService {

    /** Изменение статуса кабинета [office] на [newStatus]
     *
     * @param userOfPrevProcess пациент закончившегося процесса TODO???
     */
    fun changeStatus(officeId: String, newStatus: LocationStatus, patientIdOfPrevProcess: String? = null)

    fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int)

    fun firstPatientInQueue(officeId: String): String?

    fun deleteFirstPatientFromQueue(officeId: String)

    fun deletePatientFromQueue(officeId: String, patientId: String)

    /**
     * Удаление пациента из информации о последнем пациенте (если он фигурирует в такой информации в каком-либо кабинете)
     */
    fun deletePatientFromLastPatientInfo(patientId: String)

    fun updateLastPatientInfo(officeId: String, patientId: String, nextOfficeId: String?)
}