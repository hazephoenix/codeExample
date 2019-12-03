package ru.viscur.dh.integration.doctorapp.api.event

/**
 * Событие: вызов отклонен
 * @property callId  ID вызова
 */
class DoctorCallDeclinedEvent(
        val callId: String
) {
}