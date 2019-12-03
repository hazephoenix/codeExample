package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.Message

/**
 * Событие: Сообщение скрыто
 * @property message сообщение которое было скрыто
 */
class MessageHidedEvent(
        val message: Message
) {
}