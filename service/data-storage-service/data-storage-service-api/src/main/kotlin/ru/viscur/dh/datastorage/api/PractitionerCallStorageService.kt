package ru.viscur.dh.datastorage.api

import ru.viscur.dh.datastorage.api.criteria.PractitionerCallCriteria
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
    fun findCalls(request: PagedCriteriaRequest<PractitionerCallCriteria>): PagedResponse<PractitionerCall>

    /**
     * Создание ссылки на ожидающий ответа вызов
     */
    fun createAwaitingRef(ref: AwaitingPractitionerCallRef)

    /**
     * Обновление ссылки на ожидающий ответа вызов
     */
    fun updateAwaitingRef(ref: AwaitingPractitionerCallRef)

    /**
     * Удаление ссылки на ожидающий ответа вызов
     */
    fun removeAwaitingRef(callId: String)

    /**
     * Получение ссылкок на все ожидающие ответа вызовы
     */
    fun getAllAwaitingRef(): List<AwaitingPractitionerCallRef>

}