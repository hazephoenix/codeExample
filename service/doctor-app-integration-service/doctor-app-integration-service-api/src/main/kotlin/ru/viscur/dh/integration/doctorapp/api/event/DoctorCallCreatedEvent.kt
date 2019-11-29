package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.DoctorCall

/**
 * Событие: Поступил новый вызов
 * @property call Вызов
 */
class DoctorCallCreatedEvent(
        val call: DoctorCall
) {
}