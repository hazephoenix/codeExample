package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.enums.LocationStatus

interface OfficeService {

    /** Изменение статуса кабинета [office] на [newStatus]
     *
     * @param userOfPrevProcess пациент закончившегося процесса TODO???
     */
    fun changeStatus(office: Location, newStatus: LocationStatus, patientIdOfPrevProcess: String? = null)

    fun addPatientToQueue(officeId: String, patientId: String, estDuration: Int)

    fun firstPatientInQueue(officeId: String): String?

    fun deleteFirstPatientFromQueue(office: Location)

    fun deletePatientFromQueue(office: Location, patient: Patient)
}