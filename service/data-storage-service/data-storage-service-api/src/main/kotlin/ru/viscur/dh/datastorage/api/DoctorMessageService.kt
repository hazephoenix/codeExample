package ru.viscur.dh.datastorage.api

import ru.viscur.dh.datastorage.api.criteria.DoctorMessageCriteria
import ru.viscur.dh.datastorage.api.model.message.DoctorMessage
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse

/**
 * Сервис для работы с сообщениями
 */
interface DoctorMessageService {

    /**
     * Получение сообщения по Id
     */
    fun byId(id: String): DoctorMessage

    /**
     * Создание сообщения
     */
    fun createMessage(message: DoctorMessage): DoctorMessage

    /**
     * Обновить сообщение
     */
    fun updateMessage(message: DoctorMessage): DoctorMessage

    /**
     * Поиск сообщений
     */
    fun findMessages(request: PagedCriteriaRequest<DoctorMessageCriteria>): PagedResponse<DoctorMessage>

}