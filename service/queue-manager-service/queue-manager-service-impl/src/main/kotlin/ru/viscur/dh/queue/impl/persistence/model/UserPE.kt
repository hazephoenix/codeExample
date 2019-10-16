package ru.viscur.dh.queue.impl.persistence.model

import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.queue.api.model.UserInQueueStatus
import ru.viscur.dh.queue.impl.now
import java.util.*
import javax.persistence.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Пациент
 *
 * TODO Patient?
 *
 * @param id id пациента
 * @param firstName имя
 * @param lastName фамилия
 * @param birthDate дата рождения
 * @param diagnostic код МКБ диагноза
 * @param type тип [Severity]
 * @param status статус [UserInQueueStatus]
 * @param updatedAt дата изменения статуса
 */
@Entity
@Table(name = "queue_patients")
data class UserPE(
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
        var type: Severity = Severity.GREEN,
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
