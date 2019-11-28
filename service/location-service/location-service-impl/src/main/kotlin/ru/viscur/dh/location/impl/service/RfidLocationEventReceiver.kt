package ru.viscur.dh.location.impl.service

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import ru.viscur.dh.location.api.LOCATION_EVENT_READER_CAUGHT_TAGS
import ru.viscur.dh.location.api.ReaderEvent

/**
 * Получение сообщений от агента из очереди artemis
 */
@Service
class RfidLocationEventReceiver(private val rfidLocationService: RfidLocationService) {

    @JmsListener(destination = LOCATION_EVENT_READER_CAUGHT_TAGS)
    fun handleReaderCaughtTags(message: ReaderEvent) {
        rfidLocationService.applyEvent(message)
    }
}
