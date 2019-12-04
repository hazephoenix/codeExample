package ru.viscur.dh.integration.doctorapp.api

import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.doctorapp.api.cmd.*
import ru.viscur.dh.integration.doctorapp.api.model.*

interface DoctorAppService {

    /**
     * Новый вызов доктора
     * @throws ru.viscur.dh.integration.doctorapp.api.exception.DoctorCallNotAllowedException в случае если
     *      вызов доктора был заблокирован (в промежутке между вызовом)
     */
    fun newCall(cmd: CreatePractitionerCallAppCmd): DoctorCallAppDto

    /**
     * Доктор принял вызов
     */
    fun acceptCall(cmd: AcceptPractitionerCallAppCmd): DoctorCallAppDto

    /**
     * Доктор отклонил вызов
     */
    fun declineCall(cmd: DeclinePractitionerCallAppCmd): DoctorCallAppDto

    /**
     * Получить список докторов, которых можно вызвать (всех).
     */
    fun findCallableDoctors(): List<PractitionerAppDto>

    /**
     * Поиск кабинетов в которые можно вызывать врачей
     */
    fun findLocations(): List<LocationAppDto>;

    /**
     * Поиск входящих сообщений для текущего пользователя
     */
    fun findIncomingCalls(request: PagedRequest): PagedResponse<DoctorCallAppDto>

    /**
     * Поиск исходящих сообщений для текущего пользователя
     */
    fun findOutcomingCall(request: PagedRequest): PagedResponse<DoctorCallAppDto>

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