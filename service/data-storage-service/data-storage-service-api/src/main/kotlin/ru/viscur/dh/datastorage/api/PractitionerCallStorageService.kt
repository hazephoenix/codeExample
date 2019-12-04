package ru.viscur.dh.datastorage.api

import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.practitioner.call.model.AwaitingPractitionerCallRef
import ru.viscur.dh.practitioner.call.model.PractitionerCall

/**
 * Сервис хранилища
 */
interface PractitionerCallStorageService {

    /**
     * Получение вызова по ID
     */
    fun byId(id: String): PractitionerCall

    /**
     * Создание вызова
     */
    fun createCall(call: PractitionerCall): PractitionerCall

    /**
     * Обновление вызова
     */
    fun updateCall(call: PractitionerCall): PractitionerCall

    /**
     * Поиск вызовов по критерию
     */
    fun findCalls(request: PagedCriteriaRequest<DoctorCallCriteria>): PagedResponse<PractitionerCall>

    fun createAwaitingRef(ref: AwaitingPractitionerCallRef)
    fun updateAwaitingRef(ref: AwaitingPractitionerCallRef)
    fun removeAwaitingRef(callId: String)

    fun getAllAwaitingRef(): List<AwaitingPractitionerCallRef>

}