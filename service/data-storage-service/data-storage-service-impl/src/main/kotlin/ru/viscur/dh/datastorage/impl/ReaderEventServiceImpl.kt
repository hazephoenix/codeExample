package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.model.ReaderEventLog
import ru.viscur.dh.datastorage.impl.entity.ReaderEventLogEntity
import ru.viscur.dh.datastorage.impl.repository.ReaderEventRepository
import ru.viscur.dh.datastorage.impl.service.ReaderEventService

@Service
class ReaderEventServiceImpl(private val readerEventRepository: ReaderEventRepository) : ReaderEventService {

    override fun findAll() = readerEventRepository.findAll().map(ReaderEventLogEntity::readerEventLog)

    override fun findById(id: Long) = readerEventRepository.findById(id).orElseThrow { IllegalStateException() }!!

    override fun create(event: ReaderEventLog): ReaderEventLog {
        return readerEventRepository.save(event.readerEventLogEntity()).readerEventLog()
    }

    override fun update(event: ReaderEventLog) {
        readerEventRepository.findById(event.id).orElseThrow { IllegalStateException("Update failed: reader event with id = '${event.id}' not found") }
        readerEventRepository.save(event.readerEventLogEntity())
    }

    override fun delete(id: Long) {
        val event = readerEventRepository.findById(id).orElseThrow { IllegalStateException("Delete failed: reader event with id = '${id}' not found") }
        readerEventRepository.delete(event)
    }
}