package ru.viscur.dh.integration.practitioner.app.api.event

import ru.viscur.dh.integration.practitioner.app.api.model.MessageAppDto

/**
 * Событие: Сообщение скрыто
 * @property message сообщение которое было скрыто
 */
class MessageHidedAppEvent(
        val message: MessageAppDto
) {
}