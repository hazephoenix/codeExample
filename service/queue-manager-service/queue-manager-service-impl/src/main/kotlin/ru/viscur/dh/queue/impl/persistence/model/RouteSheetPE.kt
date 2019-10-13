package ru.viscur.dh.queue.impl.persistence.model


/**
 * Created at 03.09.2019 13:56 by SherbakovaMA
 *
 * Маршрутный лист пациента
 *
 * @param user пациент
 * @param surveys обследования, которые необходимо пройти
 */
class RouteSheetPE (
        val user: UserPE,
        val surveys: List<RouteSheetItemPE> = listOf()
){
    override fun toString(): String {
        return "RouteSheet of $user:\n    ${surveys.joinToString("\n    ")}"
    }
}