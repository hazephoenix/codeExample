package ru.viscur.dh.queue.impl

import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import ru.viscur.dh.fhir.model.dto.LocationMonitorDto
import ru.viscur.dh.queue.api.QueueStateDispatcher

/**
 * Created at 19.11.2019 10:39 by SherbakovaMA
 */
@Service
class QueueStateDispatcherImpl(
        private val jmsTemplate: JmsTemplate
) : QueueStateDispatcher {

    override fun add(locationMonitorDto: LocationMonitorDto) {
        jmsTemplate.convertAndSend("location-monitor", locationMonitorDto)
    }
}