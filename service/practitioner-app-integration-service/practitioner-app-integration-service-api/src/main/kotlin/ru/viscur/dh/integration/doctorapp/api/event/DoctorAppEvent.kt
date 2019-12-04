package ru.viscur.dh.integration.doctorapp.api.event

/**
 * Событие для отправки приложению врача
 * @property content содержимое сообщения
 * @property targetUsersIds пользователи, которым необходимо отправить сообщение.
 *      Если null, то отправка происходит всем подключенным пользователям без исключения
 */
class DoctorAppEvent(
        val content: Any,
        val targetUsersIds: Set<String>? = null

) {
}