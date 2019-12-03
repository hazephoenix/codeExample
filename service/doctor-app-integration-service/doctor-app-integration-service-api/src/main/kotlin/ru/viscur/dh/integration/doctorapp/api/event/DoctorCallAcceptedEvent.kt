package ru.viscur.dh.integration.doctorapp.api.event

/**
 * Событие: Вызов принят
 * @property callId ID вызова
 * @property timeToArrival время до прибытия
 */
class DoctorCallAcceptedEvent(
        val callId: String,
        val timeToArrival: Short
) {
}