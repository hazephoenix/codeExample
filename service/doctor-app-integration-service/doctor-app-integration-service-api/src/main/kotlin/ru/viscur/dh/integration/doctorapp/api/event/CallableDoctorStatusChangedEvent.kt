package ru.viscur.dh.integration.doctorapp.api.event

/**
 * Событие: Изменился сатус врача доступного для выбора
 * @property doctorId ID врача, статус которого изменился
 * @property disabled Доступен врач для вызова или нет
 */
class CallableDoctorStatusChangedEvent(
        val doctorId: String,
        val disabled: Boolean
) {
}