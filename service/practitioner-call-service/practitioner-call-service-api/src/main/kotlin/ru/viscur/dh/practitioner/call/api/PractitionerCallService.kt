package ru.viscur.dh.practitioner.call.api

import ru.viscur.dh.practitioner.call.api.cmd.AcceptPractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.cmd.CreatePractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.cmd.DeclinePractitionerCallCmd
import ru.viscur.dh.practitioner.call.model.PractitionerCall

/**
 * Сервис для работы с вызовом врачей
 */
interface PractitionerCallService {

    /**
     * Получение вызова по Id
     */
    fun byId(id: String): PractitionerCall

    /**
     * Создание нового вызова врача
     * @param cmd команда на создание врача
     *
     * @see CreatePractitionerCallCmd
     * @see ru.viscur.dh.practitioner.call.api.event.PractitionerCallCreatedEvent
     */
    fun createCall(cmd: CreatePractitionerCallCmd): PractitionerCall

    /**
     * Принятие вызова
     * @param cmd команда приема вызова врача
     *
     * @see AcceptPractitionerCallCmd
     * @see ru.viscur.dh.practitioner.call.api.event.PractitionerCallAcceptedEvent
     */
    fun acceptCall(cmd: AcceptPractitionerCallCmd): PractitionerCall

    /**
     * Отклонение вызова
     * @param cmd команда отклонения вызова врача
     *
     * @see DeclinePractitionerCallCmd
     * @see ru.viscur.dh.practitioner.call.api.event.PractitionerCallDeclinedEvent
     */
    fun declineCall(cmd: DeclinePractitionerCallCmd): PractitionerCall
}