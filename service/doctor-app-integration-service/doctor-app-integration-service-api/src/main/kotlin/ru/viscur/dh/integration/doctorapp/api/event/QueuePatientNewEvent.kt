package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.QueuePatient

/**
 * Событие: Новый пациент в очереди
 * @property patient Новый пациент
 */
class QueuePatientNewEvent(
        val patient: QueuePatient
) {
}