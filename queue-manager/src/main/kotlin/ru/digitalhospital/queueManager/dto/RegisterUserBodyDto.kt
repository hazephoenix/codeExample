package ru.digitalhospital.queueManager.dto

import ru.digitalhospital.queueManager.entities.User

/**
 * Created at 11.09.2019 9:42 by SherbakovaMA
 *
 * Информация для регистрации пациента в очереди
 *
 * @param user пациент
 * @param surveys обследования которые нужно пройти: id обследования и приоритет
 */
class RegisterUserBodyDto(
        val user: User,
        val surveys: List<Pair<Long, Double>>
)