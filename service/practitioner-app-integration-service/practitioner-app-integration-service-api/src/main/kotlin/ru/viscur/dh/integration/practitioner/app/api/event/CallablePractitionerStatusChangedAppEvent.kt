package ru.viscur.dh.integration.practitioner.app.api.event

/**
 * Событие: Изменился сатус врача доступного для выбора
 * @property practitionerId ID врача, статус которого изменился
 * @property disabled Доступен врач для вызова или нет
 */
class CallablePractitionerStatusChangedAppEvent(
        val practitionerId: String,
        val disabled: Boolean
) {
}