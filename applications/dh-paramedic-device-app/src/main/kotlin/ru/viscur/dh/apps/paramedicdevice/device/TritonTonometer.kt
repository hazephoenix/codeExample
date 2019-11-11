package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.dto.TritonTonometerResponse
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType
import triton.TritonMonitorDevice
import triton.protocol.enums.ProbeFlags
import triton.protocol.packet.ValuePacketData
import java.util.concurrent.TimeUnit

@Component
@Profile("triton-monitor")
class TritonTonometer(
        private val publisher: ApplicationEventPublisher,
        private val monitor: TritonMonitorDevice
) {

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Tonometer) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    private fun doMeasure(task: Task) {
        var valuePacketDataListener: ((TritonMonitorDevice.ListenerArgs<ValuePacketData>) -> Unit)? = null
        try {
            monitor.waitConnection(15, TimeUnit.SECONDS)
            monitor.changeModeThrowError(TritonMonitorDevice.Mode.W1)
            var started = false
            valuePacketDataListener = monitor.addValueDataListener {
                val value = it.value
                val nibpCycleStarted = ProbeFlags.NibpCycleStarted.memberOfFlagsMask(value.probeFlags)
                val nibpError = ProbeFlags.NibpMeasureError.memberOfFlagsMask(value.probeFlags)
                if (nibpError) {
                    // Девайс прислал ошибку
                    log.error("Got nibpError")
                    publisher.publishEvent(TaskError(task))
                } else if (nibpCycleStarted && !started) {
                    // Запущено
                    started = true
                } else if (started && !nibpCycleStarted) {
                    // Закончили измерение
                    monitor.removeValueDataListener(it.currentListener)
                    task.result = TritonTonometerResponse(
                            value.nibpSys.toInt(),
                            value.nibpDsys.toInt(),
                            value.nibpMed.toInt()
                    )
                    publisher.publishEvent(TaskComplete(task))
                }
            }
            monitor.measurePressureThrowError()
            publisher.publishEvent(TaskStarted(task))

        } catch (e: Exception) {
            valuePacketDataListener?.let { monitor.removeValueDataListener(it) }
            log.error("{}", e.message, e)
            publisher.publishEvent(TaskError(task))
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TritonTonometer::class.java)
    }

}
