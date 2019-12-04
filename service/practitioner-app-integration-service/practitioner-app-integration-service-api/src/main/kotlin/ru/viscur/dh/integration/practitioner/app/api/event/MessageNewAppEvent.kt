package ru.viscur.dh.integration.practitioner.app.api.event

import ru.viscur.dh.integration.practitioner.app.api.model.MessageAppDto

/**
 * Событие: новое сообщение
 * @property message сообщение
 */
class MessageNewAppEvent(
        val message: MessageAppDto
) {
}