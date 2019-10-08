package ru.digitalhospital.queueManager.service

import org.springframework.stereotype.Service
import ru.digitalhospital.queueManager.dto.UserInQueueStatus
import ru.digitalhospital.queueManager.entities.User
import ru.digitalhospital.queueManager.entities.UserProcessHistory
import ru.digitalhospital.queueManager.msToSeconds
import ru.digitalhospital.queueManager.now
import ru.digitalhospital.queueManager.repository.UserProcessHistoryRepository
import ru.digitalhospital.queueManager.repository.UserRepository
import java.util.*

/**
 * Created at 04.09.2019 8:52 by SherbakovaMA
 *
 * Сервис для работы с пациентами
 */
@Service
class UserService(
        private val userRepository: UserRepository,
        private val userProcessHistoryRepository: UserProcessHistoryRepository
) {
    /**
     * Изменение статуса пациента [user] на [newStatus]
     * @param officeIdOfPrevProcess кабинет предыдущей операции.
     *  используется, если пред процесс пациента привязан к какому-либо кабинету (например, осмотр)
     * @param saveCurrentStatusToHistory сохранить текущий статус с продолжительностью нахождения в историю.
     *  В редких случаях не нужно сохранять, например, если было обследование, но оно не было завершено корректно, т к
     *  пациент покинул очередь совсем или временно
     */
    fun changeStatus(user: User, newStatus: UserInQueueStatus, officeIdOfPrevProcess: Long? = null, saveCurrentStatusToHistory: Boolean = true) {
        val now = now()
        if (saveCurrentStatusToHistory) {
            saveCurrentStatus(user, officeIdOfPrevProcess, now)
        }
        userRepository.save(user.apply {
            status = newStatus
            updatedAt = now
        })
    }

    /**
     * Сохранение текущего статуса в историю
     */
    fun saveCurrentStatus(user: User, officeIdOfPrevProcess: Long? = null, now: Date = now()) {
        userProcessHistoryRepository.save(UserProcessHistory(
                userId = user.id,
                officeId = officeIdOfPrevProcess,
                status = user.status,
                fireDate = user.updatedAt,
                duration = msToSeconds(now.time - user.updatedAt.time)
        ))
    }
}