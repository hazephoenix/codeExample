package ru.viscur.dh.integration.practitioner.app.api.event

/**
 * Событие: Врач удален (более не доступен для выбора)
 * @property practitionerId  ID врача
 */
class CallablePractitionerLostAppEvent(
        val practitionerId: String
)