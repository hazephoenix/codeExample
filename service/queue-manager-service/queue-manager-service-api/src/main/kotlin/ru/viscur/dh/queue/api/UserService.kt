package ru.viscur.dh.queue.api

import ru.viscur.dh.queue.api.model.User
import ru.viscur.dh.queue.api.model.UserInQueueStatus
import java.util.*

interface UserService {

    /**
     * Изменение статуса пациента [user] на [newStatus]
     * @param officeIdOfPrevProcess кабинет предыдущей операции.
     *  используется, если пред процесс пациента привязан к какому-либо кабинету (например, осмотр)
     * @param saveCurrentStatusToHistory сохранить текущий статус с продолжительностью нахождения в историю.
     *  В редких случаях не нужно сохранять, например, если было обследование, но оно не было завершено корректно, т к
     *  пациент покинул очередь совсем или временно
     */
    fun changeStatus(user: User, newStatus: UserInQueueStatus, officeIdOfPrevProcess: Long? = null, saveCurrentStatusToHistory: Boolean = true)

    /**
     * Сохранение текущего статуса в историю
     */
    fun saveCurrentStatus(user: User, officeIdOfPrevProcess: Long? = null, now: Date)
}