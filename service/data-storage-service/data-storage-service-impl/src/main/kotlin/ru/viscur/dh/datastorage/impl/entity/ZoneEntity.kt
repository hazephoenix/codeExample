package ru.viscur.dh.datastorage.impl.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Зона (область помещения для определения местоположения сотрудников)
 * @param zoneId id зоны
 * @param name Наименование зоны
 * @param officeIds перечисленные через запятую id помещений покрываемых зоной
 */
@Entity
@Table(name = "zone")
data class ZoneEntity(
        @Id
        @Column(name = "zone_id", nullable = false)
        val zoneId: String,
        @Column(nullable = false)
        var name: String,
        @Column(name = "office_ids")
        var officeIds: String? = null
)
