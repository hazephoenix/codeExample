package ru.viscur.dh.paramedic.devicemonitor.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.viscur.dh.paramedic.devicemonitor.dto.Observation
import ru.viscur.dh.paramedic.devicemonitor.events.MedMetricRequestEvent
import ru.viscur.dh.paramedic.devicemonitor.meddevice.IMedDevice
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * Created at 01.10.2019 17:30 by TimochkinEA
 *
 * Менеджер для управления оборудованием
 */
@Service
class DeviceManager(private val medDevices: List<IMedDevice>) {

    private val log: Logger = LoggerFactory.getLogger(DeviceManager::class.java)

    @EventListener(MedMetricRequestEvent::class)
    fun medRequestListener(event: MedMetricRequestEvent) {
        log.debug("New request for metrics: ${event.request}")
    }
}
