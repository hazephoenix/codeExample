package ru.digitalhospital.queueManager.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.digitalhospital.queueManager.entities.UserProcessHistory

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для истории статусов пациентов
 */
@Repository
interface UserProcessHistoryRepository : CrudRepository<UserProcessHistory, Long>
