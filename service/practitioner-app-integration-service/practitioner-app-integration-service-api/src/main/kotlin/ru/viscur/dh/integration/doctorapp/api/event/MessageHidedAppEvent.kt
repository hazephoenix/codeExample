package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.MessageAppDto

/**
 * Событие: Сообщение скрыто
 * @property message сообщение которое было скрыто
 */
class MessageHidedAppEvent(
        val message: MessageAppDto
) {
}