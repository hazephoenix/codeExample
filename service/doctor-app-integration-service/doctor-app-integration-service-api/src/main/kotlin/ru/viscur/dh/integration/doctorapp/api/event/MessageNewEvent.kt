package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.Message

/**
 * Событие: новое сообщение
 * @property message сообщение
 */
class MessageNewEvent(
        val message: Message
) {
}