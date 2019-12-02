package ru.viscur.dh.datastorage.impl.service

import ru.viscur.dh.datastorage.api.model.Zone

interface ZoneService {
    fun findAll(): List<Zone>
    fun findById(zoneId: String): Zone?
    fun create(zone: Zone): Zone
    fun update(zone: Zone)
    fun delete(zoneId: String)
    fun findAllById(ids: Iterable<String>): List<Zone>
}
