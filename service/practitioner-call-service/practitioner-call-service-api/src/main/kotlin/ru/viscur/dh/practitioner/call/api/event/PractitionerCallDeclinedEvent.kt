package ru.viscur.dh.practitioner.call.api.event

/**
 * Событие: вызов отклонен
 * @property callId  ID вызова
 */
class PractitionerCallDeclinedEvent(
        val callId: String
) {
}