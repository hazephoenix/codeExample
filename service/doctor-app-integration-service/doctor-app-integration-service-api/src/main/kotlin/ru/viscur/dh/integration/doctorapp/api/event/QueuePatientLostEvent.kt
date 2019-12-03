package ru.viscur.dh.integration.doctorapp.api.event

/**
 * Событие: Пациент убран из очереди
 * @property patientId ID пациента
 */
class QueuePatientLostEvent(
        val patientId: String
) {
}