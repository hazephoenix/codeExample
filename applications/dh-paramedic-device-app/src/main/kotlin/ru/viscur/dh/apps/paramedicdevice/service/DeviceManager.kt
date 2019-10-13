package ru.viscur.dh.apps.paramedicdevice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.viscur.dh.apps.paramedicdevice.events.MedMetricRequestEvent
import ru.viscur.dh.apps.paramedicdevice.meddevice.IMedDevice

/**
 * Created at 01.10.2019 17:30 by TimochkinEA
 *
 * Менеджер для управления оборудованием
 */
@Service
class DeviceManager(private val medDevices: List<IMedDevice>) {

    private val log: Logger = LoggerFactory.getLogger(DeviceManager::class.java)

    @Autowired
    private lateinit var mapper: ObjectMapper

    @EventListener(MedMetricRequestEvent::class)
    fun medRequestListener(event: MedMetricRequestEvent) {
        val request = event.request
        log.debug("New request for metrics: $request")
        log.debug("Reference: ${request.encounter.value.resourceType}/${request.encounter.value.identifier.value}")
        log.debug("Request after serialize: ${mapper.writeValueAsString(request)}")

    }
}
