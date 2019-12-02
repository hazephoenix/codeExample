package ru.viscur.dh.datastorage.impl

import ru.viscur.dh.datastorage.api.model.ReaderEventLog
import ru.viscur.dh.datastorage.api.model.Tag
import ru.viscur.dh.datastorage.api.model.Zone
import ru.viscur.dh.datastorage.impl.entity.ReaderEventLogEntity
import ru.viscur.dh.datastorage.impl.entity.TagEntity
import ru.viscur.dh.datastorage.impl.entity.ZoneEntity

fun ZoneEntity.zone() = Zone(zoneId, name, officeIds?.split(",") ?: emptyList())
fun Zone.entity() = ZoneEntity(zoneId, name, officeIds.joinToString(",").takeIf(String::isNotBlank))

fun TagEntity.tag() = Tag(tagId, practitionerId)
fun Tag.entity() = TagEntity(tagId, practitionerId)

fun ReaderEventLogEntity.readerEventLog() = ReaderEventLog(id, stamp, reader, channel, zone, tags.split(","))
fun ReaderEventLog.readerEventLogEntity() = ReaderEventLogEntity(id, stamp, reader, channel, zone, tags.joinToString(","))
