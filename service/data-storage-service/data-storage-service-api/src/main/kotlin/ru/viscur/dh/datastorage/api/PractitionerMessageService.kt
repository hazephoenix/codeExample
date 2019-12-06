package ru.viscur.dh.datastorage.api

import ru.viscur.dh.datastorage.api.criteria.PractitionerMessageCriteria
import ru.viscur.dh.datastorage.api.model.message.PractitionerMessage
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse

/**
 * Сервис для работы с сообщениями
 */
interface PractitionerMessageService {

    /**
     * Получение сообщения по Id
     */
    fun byId(id: String): PractitionerMessage

    /**
     * Создание сообщения
     */
    fun createMessage(message: PractitionerMessage): PractitionerMessage

    /**
     * Обновить сообщение
     */
    fun updateMessage(message: PractitionerMessage): PractitionerMessage

    /**
     * Поиск сообщений
     */
    fun findMessages(request: PagedCriteriaRequest<PractitionerMessageCriteria>): PagedResponse<PractitionerMessage>

}