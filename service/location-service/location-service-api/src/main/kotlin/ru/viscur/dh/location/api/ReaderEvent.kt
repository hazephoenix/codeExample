package ru.viscur.dh.location.api

import java.time.Instant

/**
 * Информация о считывании:
 * @param stamp момент регистрации события
 * @param reader идентификатор rfid-считывателя
 * @param channel идентификатор антренны считывателя
 * @param zone идентификатор зоны
 * @param tags список зарегистрированных идентификаторов тегов
 */
data class ReaderEvent(
        val stamp: Instant,
        val reader: String,
        val channel: String,
        val zone: String,
        val tags: Collection<String>
) : Comparable<ReaderEvent?> {
    override fun compareTo(other: ReaderEvent?) = stamp.compareTo(other?.stamp)
}
