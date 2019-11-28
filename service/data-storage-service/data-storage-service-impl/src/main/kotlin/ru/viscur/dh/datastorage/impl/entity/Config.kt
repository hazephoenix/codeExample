package ru.viscur.dh.datastorage.impl.entity

import javax.persistence.*

/**
 * Created at 05.11.2019 15:01 by SherbakovaMA
 *
 * Настройки системы
 *
 * @param id id записи
 * @param code код
 * @param value значение
 */
@Entity
@Table(name = "config")
@SequenceGenerator(name = "pk_seq", sequenceName = "pk_seq", allocationSize = 1)
data class Config(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_seq")
        var id: Long = 0L,
        @Column
        var code: String? = null,
        @Column
        var value: String? = null
)