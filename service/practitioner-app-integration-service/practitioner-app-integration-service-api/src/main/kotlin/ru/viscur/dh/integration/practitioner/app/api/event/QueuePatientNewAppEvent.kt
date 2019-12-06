package ru.viscur.dh.integration.practitioner.app.api.event

import ru.viscur.dh.integration.practitioner.app.api.model.QueuePatientAppDto

/**
 * Событие: Новый пациент в очереди
 * @property patient Новый пациент
 */
class QueuePatientNewAppEvent(
        val patient: QueuePatientAppDto
) {
}