package ru.viscur.dh.practitioner.call.api.event

import ru.viscur.dh.practitioner.call.model.PractitionerCall


/**
 * Событие: Поступил новый вызов
 * @property call Вызов
 */
class PractitionerCallCreatedEvent(
        val call: PractitionerCall
) {
}