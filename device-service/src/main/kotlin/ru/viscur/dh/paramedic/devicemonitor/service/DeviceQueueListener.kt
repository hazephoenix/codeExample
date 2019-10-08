package ru.viscur.dh.paramedic.devicemonitor.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import ru.viscur.dh.paramedic.devicemonitor.dto.ServiceRequest
import ru.viscur.dh.paramedic.devicemonitor.events.MedMetricRequestEvent

/**
 * Created at 01.10.2019 17:43 by TimochkinEA
 *
 * Общий слушатель очереди запросов данных от устройст
 */
@Component
class DeviceQueueListener {

    private val log: Logger = LoggerFactory.getLogger(DeviceQueueListener::class.java)

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var publisher: ApplicationEventPublisher

    @JmsListener(destination = "\${uid}-paramedic-requests")
    fun incoming(request: ObjectNode) {
        val r = mapper.treeToValue(request, ServiceRequest::class.java)

        if (log.isDebugEnabled) log.debug(r.toString())
        publisher.publishEvent(MedMetricRequestEvent(r))
    }
}
