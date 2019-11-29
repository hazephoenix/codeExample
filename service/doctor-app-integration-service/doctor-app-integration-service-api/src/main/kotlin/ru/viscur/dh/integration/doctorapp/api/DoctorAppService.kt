package ru.viscur.dh.integration.doctorapp.api

import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.doctorapp.api.cmd.*
import ru.viscur.dh.integration.doctorapp.api.model.*
import ru.viscur.dh.integration.doctorapp.api.request.DoctorCallsRequest
import ru.viscur.dh.integration.doctorapp.api.response.DoctorCallsResponse

interface DoctorAppService {

    /**
     * Новый вызов доктора
     * @throws ru.viscur.dh.integration.doctorapp.api.exception.DoctorCallNotAllowedException в случае если
     *      вызов доктора был заблокирован (в промежутке между вызовом)
     */
    fun newCall(cmd: NewDoctorCallCmd): DoctorCall

    /**
     * Доктор принял вызов
     */
    fun acceptCall(cmd: AcceptDoctorCallCmd): DoctorCall

    /**
     * Доктор отклонил вызов
     */
    fun declineCall(cmd: DeclineDoctorCallCmd): DoctorCall

    /**
     * Получить список докторов, которых можно вызвать (всех).
     */
    fun findCallableDoctors(): List<CallableDoctor>

    /**
     * Поиск кабинетов в которые можно вызывать врачей
     */
    fun findLocations(): List<Location>;

    /**
     * Статус доктора изменился (он перешел в интенсивную терапию или вышел из нее)
     */
    fun callableDoctorStatusChanged(doctor: CallableDoctorStatusChangedCmd)

    /**
     * Поиск входящих сообщений для текущего пользователя
     */
    fun findIncomingCalls(request: PagedRequest): PagedResponse<DoctorCall>

    /**
     * Поиск исходящих сообщений для текущего пользователя
     */
    fun findOutcomingCall(request: PagedRequest): PagedResponse<DoctorCall>

    /**
     * Получение списка пациентов в очереди для текущего пользователя
     */
    fun getQueuePatients(): List<QueuePatient>

    /**
     * Получение сообщений
     */
    fun findMessages(request: PagedRequest, actual: Boolean): PagedResponse<Message>

    /**
     * Скрыть сообщение
     */
    fun hideMessage(messageId: String): Message


}