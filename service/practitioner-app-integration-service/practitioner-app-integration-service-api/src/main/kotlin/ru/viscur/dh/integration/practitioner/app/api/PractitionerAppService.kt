package ru.viscur.dh.integration.practitioner.app.api

import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.practitioner.app.api.cmd.AcceptPractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.cmd.CreatePractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.cmd.DeclinePractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.model.*

interface PractitionerAppService {

    /**
     * Новый вызов доктора
     */
    fun newCall(cmd: CreatePractitionerCallAppCmd): PractitionerCallAppDto

    /**
     * Доктор принял вызов
     */
    fun acceptCall(cmd: AcceptPractitionerCallAppCmd): PractitionerCallAppDto

    /**
     * Доктор отклонил вызов
     */
    fun declineCall(cmd: DeclinePractitionerCallAppCmd): PractitionerCallAppDto

    /**
     * Получить список докторов, которых можно вызвать (всех).
     */
    fun findCallablePractitioners(): List<PractitionerAppDto>

    /**
     * Поиск кабинетов в которые можно вызывать врачей
     */
    fun findLocations(): List<LocationAppDto>;

    /**
     * Поиск входящих сообщений для текущего пользователя
     */
    fun findIncomingCalls(request: PagedRequest): PagedResponse<PractitionerCallAppDto>

    /**
     * Поиск исходящих сообщений для текущего пользователя
     */
    fun findOutcomingCall(request: PagedRequest): PagedResponse<PractitionerCallAppDto>

    /**
     * Получение списка пациентов в очереди для текущего пользователя
     */
    fun getQueuePatients(): List<QueuePatientAppDto>

    /**
     * Получение сообщений
     */
    fun findMessages(request: PagedRequest, actual: Boolean): PagedResponse<MessageAppDto>

    /**
     * Скрыть сообщение
     */
    fun hideMessage(messageId: String): MessageAppDto


}