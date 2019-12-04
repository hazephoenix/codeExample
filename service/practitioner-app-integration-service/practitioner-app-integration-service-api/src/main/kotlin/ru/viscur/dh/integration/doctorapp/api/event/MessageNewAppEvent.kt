package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.MessageAppDto

/**
 * Событие: новое сообщение
 * @property message сообщение
 */
class MessageNewAppEvent(
        val message: MessageAppDto
) {
}