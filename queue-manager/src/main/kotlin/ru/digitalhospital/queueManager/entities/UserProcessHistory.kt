package ru.digitalhospital.queueManager.entities

import ru.digitalhospital.queueManager.dto.UserInQueueStatus
import java.util.*
import javax.persistence.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * История статусов пациента (события изменения статусов
 *
 * @param id id записи
 * @param userId id пациента
 * @param officeId id кабинета. если процесс связан с кабинетом (пациент в очереди опр. кабинет или на осмотре)
 * @param status статус пациента [UserInQueueStatus]
 * @param fireDate дата события
 * @param duration продолжительность события (в сек)
 */
@Entity
@Table(name = "user_process_history")
data class UserProcessHistory(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_process_history_seq")
        @SequenceGenerator(name = "user_process_history_seq", sequenceName = "user_process_history_seq", allocationSize = 0)
        var id: Long = 0L,
        @Column
        var userId: Long? = null,
        @Column
        var officeId: Long? = null,
        @Column
        @Enumerated(EnumType.STRING)
        var status: UserInQueueStatus? = null,
        @Column
        var fireDate: Date? = null,
        @Column
        var duration: Int? = null
)
