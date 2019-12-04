package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.QueuePatientAppDto

/**
 * Событие: Новый пациент в очереди
 * @property patient Новый пациент
 */
class QueuePatientNewAppEvent(
        val patient: QueuePatientAppDto
) {
}