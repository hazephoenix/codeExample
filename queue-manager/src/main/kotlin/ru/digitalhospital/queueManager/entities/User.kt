package ru.digitalhospital.queueManager.entities

import ru.digitalhospital.queueManager.dto.UserInQueueStatus
import ru.digitalhospital.queueManager.dto.UserType
import ru.digitalhospital.queueManager.now
import java.util.*
import javax.persistence.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Пациент
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
@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
        @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 0)
        var id: Long = 0L,
        @Column
        var firstName: String? = null,
        @Column
        var lastName: String? = null,
        @Column
        var birthDate: Date? = null,
        @Column
        var diagnostic: String? = null,
        @Column
        @Enumerated(EnumType.STRING)
        var type: UserType = UserType.GREEN,
        @Column
        @Enumerated(EnumType.STRING)
        var status: UserInQueueStatus? = null,
        @Column
        var updatedAt: Date = now()
) {
        override fun toString(): String {
                return "User(id=$id, $firstName, type=$type, status=$status)"
        }
}
