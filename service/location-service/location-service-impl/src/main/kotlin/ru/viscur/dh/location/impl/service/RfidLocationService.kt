package ru.viscur.dh.location.impl.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.impl.service.ReaderEventService
import ru.viscur.dh.location.api.ReaderEvent
import ru.viscur.dh.location.impl.readerEventLog
import java.time.Duration
import java.time.Instant


@Service
class RfidLocationService(
    private val positions: Positions,
    private val readerEventService: ReaderEventService
) {

    fun usedZones() = positions.map { (_, zoneId) -> zoneId }

    fun usedTags() = positions.map { (tagId, _) -> tagId }

    fun findTag(tagId: String) = positions[tagId]

    fun listZoneTags(zoneId: String) = positions.filter { (_, id) -> zoneId == id }.map { (tagId, _) -> tagId }

    fun applyEvent(message: ReaderEvent) {
        positions.putAll(message.tags.map { it to message.zone })
        readerEventService.create(message.readerEventLog())
        val duration = Duration.between(message.stamp, Instant.now())
        log.info("rfid event: [${message.reader}:${message.channel}] ${message.zone}: ${message.tags} (lag: ${duration.toMillis()})")
    }

    companion object {
        private val log = LoggerFactory.getLogger(RfidLocationService::class.java)!!
    }
}
