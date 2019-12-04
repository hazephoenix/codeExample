package ru.viscur.dh.practitioner.call.api.event

/**
 * Событие: Вызов принят
 * @property callId ID вызова
 * @property timeToArrival время до прибытия
 */
class PractitionerCallAcceptedEvent(
        val callId: String,
        val timeToArrival: Short
) {
}