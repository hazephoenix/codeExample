package ru.viscur.dh.datastorage.impl.entity

import java.time.Instant
import javax.persistence.*

/**
 * Информация о считывании:
 * @param id суррогатный id в БД
 * @param stamp момент регистрации события
 * @param reader идентификатор rfid-считывателя
 * @param channel идентификатор антренны считывателя
 * @param zone идентификатор зоны
 * @param tags список зарегистрированных идентификаторов тегов
 */
@Entity
@Table(name = "reader_event_log")
@SequenceGenerator(name = "pk_seq", sequenceName = "pk_seq", allocationSize = 1)
data class ReaderEventLogEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_seq")
        var id: Long,
        @Column(nullable = false)
        var stamp: Instant,
        @Column(nullable = false)
        var reader: String,
        @Column(nullable = false)
        var channel: String,
        @Column(nullable = false)
        var zone: String,
        @Column(nullable = false)
        var tags: String
)
