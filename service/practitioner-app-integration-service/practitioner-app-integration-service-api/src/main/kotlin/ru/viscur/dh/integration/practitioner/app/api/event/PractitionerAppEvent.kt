package ru.viscur.dh.integration.practitioner.app.api.event

/**
 * Событие для отправки приложению врача
 * @property content содержимое сообщения
 * @property targetUsersIds пользователи, которым необходимо отправить сообщение.
 *      Если null, то отправка происходит всем подключенным пользователям без исключения
 */
class PractitionerAppEvent(
        val content: Any,
        val targetUsersIds: Set<String>? = null

) {
}