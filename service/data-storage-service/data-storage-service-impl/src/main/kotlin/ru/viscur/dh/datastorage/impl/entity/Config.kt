package ru.viscur.dh.datastorage.impl.entity

import javax.persistence.*

/**
 * Created at 05.11.2019 15:01 by SherbakovaMA
 *
 * Настройки системы
 */
@Entity
@Table(name = "config")
@SequenceGenerator(name = "pk_seq", sequenceName = "pk_seq", allocationSize = 1)
data class Config(
        /**
         * ID записи
         */
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_seq")
        var id: Long = 0L,

        /**
         * Код
         */
        @Column
        var code: String? = null,

        /**
         * Значение
         */
        @Column
        var value: String? = null
)