package ru.viscur.dh.queue.api.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Пациент
 *
 * TODO: Patient?
 *
 * @param id id пациента
 * @param firstName имя
 * @param lastName фамилия
 * @param birthDate дата рождения
 * @param diagnostic код МКБ диагноза
 * @param type тип [UserType]
 * @param status статус [UserInQueueStatus]
 * @param updatedAt дата изменения статуса
 */
data class User(
        var id: Long = 0L,
        var firstName: String? = null,
        var lastName: String? = null,
        var birthDate: Date? = null,
        var diagnostic: String? = null,
        var type: UserType = UserType.GREEN,
        var status: UserInQueueStatus? = null,
        var updatedAt: Date = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant())
) {
        override fun toString(): String {
                return "User(id=$id, $firstName, type=$type, status=$status)"
        }
}
