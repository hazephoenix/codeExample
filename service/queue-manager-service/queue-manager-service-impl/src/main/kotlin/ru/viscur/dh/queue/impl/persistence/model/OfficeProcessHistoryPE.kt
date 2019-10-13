package ru.viscur.dh.queue.impl.persistence.model

import ru.viscur.dh.queue.api.model.OfficeStatus
import ru.viscur.dh.queue.api.model.UserType
import java.util.*
import javax.persistence.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * История событий кабинета
 * Поля, относящиеся к пациенту заполняются, если событие имеет отношение к пациенту (статус: ожидание пациента, осмотр)
 *
 * @param id id записи
 * @param surveyTypeId id типа осмотра
 * @param status статус кабинета [OfficeStatus]
 * @param fireDate дата события
 * @param duration продолжительность события (в сек)
 * @param userType тип пациента [UserType]
 * @param userDiagnostic код МКБ диагноза пациента
 * @param userAgeGroup возрастная группа пациента [utils.ageGroup]
 */
@Entity
@Table(name = "office_process_history")
data class OfficeProcessHistoryPE(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_process_history_seq")
        @SequenceGenerator(name = "office_process_history_seq", sequenceName = "office_process_history_seq", allocationSize = 0)
        var id: Long = 0L,
        @Column
        var surveyTypeId: Long? = null,
        @Column
        @Enumerated(EnumType.STRING)
        var status: OfficeStatus? = null,
        @Column
        var fireDate: Date? = null,
        @Column
        var duration: Int? = null,
        @Column
        @Enumerated(EnumType.STRING)
        var userType: UserType? = null,
        @Column
        var userDiagnostic: String? = null,
        @Column
        var userAgeGroup: Int? = null
)
