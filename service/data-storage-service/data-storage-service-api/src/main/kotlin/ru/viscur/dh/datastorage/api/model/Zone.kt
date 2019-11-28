package ru.viscur.dh.datastorage.api.model

/**
 * Зона (область помещения для определения местоположения сотрудников)
 * @param zoneId id зоны
 * @param name Наименование зоны
 * @param officeIds список id помещений покрываемых зоной
 */
data class Zone(
        val zoneId: String,
        var name: String,
        var officeIds: List<String> = emptyList()
)
