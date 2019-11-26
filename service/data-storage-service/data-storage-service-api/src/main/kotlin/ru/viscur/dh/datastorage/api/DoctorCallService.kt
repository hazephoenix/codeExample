package ru.viscur.dh.datastorage.api

import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.model.call.DoctorCall
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse

interface DoctorCallService {

    /**
     * Получение вызова по ID
     */
    fun byId(id: String): DoctorCall

    /**
     * Создание вызова
     */
    fun createDoctorCall(call: DoctorCall): DoctorCall

    /**
     * Обновление вызова
     */
    fun updateDoctorCall(call: DoctorCall): DoctorCall

    /**
     * Поиск вызовов по критерию
     */
    fun findCalls(request: PagedCriteriaRequest<DoctorCallCriteria>): PagedResponse<DoctorCall>

}