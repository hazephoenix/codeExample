package ru.viscur.dh.datastorage.impl.service

import ru.viscur.dh.datastorage.api.model.ReaderEventLog
import ru.viscur.dh.datastorage.impl.entity.ReaderEventLogEntity

interface ReaderEventService {
    fun findAll(): List<ReaderEventLog>
    fun findById(id: Long): ReaderEventLogEntity?
    fun create(event: ReaderEventLog): ReaderEventLog
    fun update(event: ReaderEventLog)
    fun delete(id: Long)
}
