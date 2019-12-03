package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.CallableDoctor

/**
 * Событие: Появился новый врач доступный для вызова
 * @property doctor Новый врач
 */
class CallableDoctorNewEvent(
        val doctor: CallableDoctor
) {
}