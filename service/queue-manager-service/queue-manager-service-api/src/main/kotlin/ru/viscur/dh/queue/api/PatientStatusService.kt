package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.queue.api.model.User
import java.util.*

interface PatientStatusService {

    /**
     * Изменение статуса пациента [user] на [newStatus]
     * @param officeIdOfPrevProcess кабинет предыдущей операции.
     *  используется, если пред процесс пациента привязан к какому-либо кабинету (например, осмотр)
     * @param saveCurrentStatusToHistory сохранить текущий статус с продолжительностью нахождения в историю.
     *  В редких случаях не нужно сохранять, например, если было обследование, но оно не было завершено корректно, т к
     *  пациент покинул очередь совсем или временно
     */
    fun changeStatus(patientId: String, newStatus: PatientQueueStatus, officeIdOfPrevProcess: String? = null, saveCurrentStatusToHistory: Boolean = true)

    /**
     * Сохранение текущего статуса в историю
     */
    fun saveCurrentStatus(patientId: String, officeIdOfPrevProcess: String? = null, now: Date)
}