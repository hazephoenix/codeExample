package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.dto.PulseoximeterResponse
import ru.viscur.dh.apps.paramedicdevice.dto.Task
import ru.viscur.dh.apps.paramedicdevice.dto.TaskType
import ru.viscur.dh.apps.paramedicdevice.events.TaskComplete
import ru.viscur.dh.apps.paramedicdevice.events.TaskError
import ru.viscur.dh.apps.paramedicdevice.events.TaskRequested
import ru.viscur.dh.apps.paramedicdevice.events.TaskStarted
import triton.TritonMonitorDevice
import triton.protocol.enums.ReturnCode
import triton.protocol.packet.ValuePacketData
import java.lang.Exception
import java.util.concurrent.TimeUnit

@Component
@Profile("triton-monitor")
class Pulseoximeter(
        private val publisher: ApplicationEventPublisher,
        private val monitor: TritonMonitorDevice
) {


    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Pulseoximeter) {
            doMeasure(task)
        }
    }

    private fun doMeasure(task: Task) {
        var valuePacketDataListener: ((TritonMonitorDevice.ListenerArgs<ValuePacketData>) -> Unit)? = null
        try {
            monitor.waitConnection(15, TimeUnit.SECONDS)
            monitor.changeModeThrowError(TritonMonitorDevice.Mode.W1)
            valuePacketDataListener = monitor.addValueDataListener {
                val value = it.value
                val spO2 = value.spO2.toInt()
                val hr = value.hr.toInt()
                val rspc = value.rspc.toInt()
                if (spO2 == 1000 || hr == 1000) {
                    // Девайс еще не посчитал, подождем следующий пакет данных
                    return@addValueDataListener
                }
                monitor.removeValueDataListener(it.currentListener)
                task.result = PulseoximeterResponse(
                        spO2, hr,
                        if (rspc == 4095) 0 else rspc
                )
                publisher.publishEvent(TaskComplete(task))
            }
            publisher.publishEvent(TaskStarted(task))
        } catch (e: Exception) {
            valuePacketDataListener?.let { monitor.removeValueDataListener(it) }
            log.error("{}", e.message, e)
            publisher.publishEvent(TaskError(task))
        }
    }


    companion object {
        val log = LoggerFactory.getLogger(Pulseoximeter::class.java)
    }
}