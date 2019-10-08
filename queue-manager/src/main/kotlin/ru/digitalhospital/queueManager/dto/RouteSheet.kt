package ru.digitalhospital.queueManager.dto

import ru.digitalhospital.queueManager.entities.RouteSheetItem
import ru.digitalhospital.queueManager.entities.User

/**
 * Created at 03.09.2019 13:56 by SherbakovaMA
 *
 * Маршрутный лист пациента
 *
 * @param user пациент
 * @param surveys обследования, которые необходимо пройти
 */
class RouteSheet (
    val user: User,
    val surveys: List<RouteSheetItem> = listOf()
){
    override fun toString(): String {
        return "RouteSheet of $user:\n    ${surveys.joinToString("\n    ")}"
    }
}