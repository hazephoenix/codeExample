package ru.viscur.dh.integration.doctorapp.api.event

/**
 * Событие: Врач удален (более не доступен для выбора)
 * @property doctorId  ID врача
 */
class CallableDoctorLostEvent(
        val doctorId: String
)