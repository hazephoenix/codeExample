package ru.viscur.dh.integration.doctorapp.api.event

import ru.viscur.dh.integration.doctorapp.api.model.PractitionerAppDto

/**
 * Событие: Появился новый врач доступный для вызова
 * @property doctor Новый врач
 */
class CallableDoctorNewAppEvent(
        val doctor: PractitionerAppDto
) {
}