package ru.viscur.dh.queue.api

import ru.viscur.dh.queue.api.model.Office
import ru.viscur.dh.queue.api.model.OfficeStatus
import ru.viscur.dh.queue.api.model.User

interface OfficeService {

    /** Изменение статуса кабинета [office] на [newStatus]
     *
     * @param userOfPrevProcess пациент закончившегося процесса TODO???
     */
    fun changeStatus(office: Office, newStatus: OfficeStatus, userOfPrevProcess: User? = null)
}