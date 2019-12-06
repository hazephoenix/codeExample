package ru.viscur.dh.datastorage.api.model

import java.time.Instant

/**
 * Информация о считывании:
 * @param stamp момент регистрации события
 * @param reader идентификатор rfid-считывателя
 * @param channel идентификатор антренны считывателя
 * @param zone идентификатор зоны
 * @param tags список зарегистрированных идентификаторов тегов
 */
data class ReaderEventLog(
        val id: Long,
        val stamp: Instant,
        val reader: String,
        val channel: String,
        val zone: String,
        val tags: Collection<String>
)
