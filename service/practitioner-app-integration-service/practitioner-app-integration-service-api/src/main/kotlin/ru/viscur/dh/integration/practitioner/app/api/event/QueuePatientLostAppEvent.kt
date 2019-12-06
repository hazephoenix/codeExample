package ru.viscur.dh.integration.practitioner.app.api.event

/**
 * Событие: Пациент убран из очереди
 * @property patientId ID пациента
 */
class QueuePatientLostAppEvent(
        val patientId: String
) {
}