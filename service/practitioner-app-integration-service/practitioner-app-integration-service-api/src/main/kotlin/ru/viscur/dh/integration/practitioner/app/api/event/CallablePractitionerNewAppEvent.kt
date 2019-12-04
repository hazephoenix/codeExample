package ru.viscur.dh.integration.practitioner.app.api.event

import ru.viscur.dh.integration.practitioner.app.api.model.PractitionerAppDto

/**
 * Событие: Появился новый врач доступный для вызова
 * @property practitioner Новый врач
 */
class CallablePractitionerNewAppEvent(
        val practitioner: PractitionerAppDto
) {
}