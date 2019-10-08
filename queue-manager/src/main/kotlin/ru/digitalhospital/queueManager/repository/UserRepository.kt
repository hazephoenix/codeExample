package ru.digitalhospital.queueManager.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.digitalhospital.queueManager.entities.User

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для пациентов
 */
@Repository
interface UserRepository : CrudRepository<User, Long>
