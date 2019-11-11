package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.dto.ElectrocardiographResponse
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType
import triton.TritonMonitorDevice
import triton.image.SvgWavesPainter
import triton.protocol.packet.ValuePacketData
import triton.protocol.packet.WavePacketData3
import java.util.concurrent.TimeUnit

@Component
@Profile("triton-monitor")
class Electrocardiograph(
        private val publisher: ApplicationEventPublisher,
        private val monitor: TritonMonitorDevice
) {


    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Electrocardiograph) {
            doMeasure(task)
        }
    }

    private fun doMeasure(task: Task) {
        var valuePacketDataListener: ((TritonMonitorDevice.ListenerArgs<ValuePacketData>) -> Unit)? = null
        var wavePacketData3Listener: ((TritonMonitorDevice.ListenerArgs<WavePacketData3>) -> Unit)? = null
        try {
            monitor.waitConnection(15, TimeUnit.SECONDS)
            val ecg1 = mutableListOf<UByteArray>()
            val ecg2 = mutableListOf<UByteArray>()
            val ecg3 = mutableListOf<UByteArray>()
            var heartRate: Int? = null
            var resp: Int? = null

            monitor.changeModeThrowError(TritonMonitorDevice.Mode.E3)
            monitor.startSendingDataThrowError()
            publisher.publishEvent(TaskStarted(task))

            val completeTaskIfReady = {
                synchronized(ecg1) {
                    if (heartRate != null && resp != null && ecg1.size == ECG_WAVES_COUNT) {
                        val painter = SvgWavesPainter()
                        task.result = ElectrocardiographResponse(
                                heartRate!!,
                                painter.createImageAsString(ecg1, "I"),
                                painter.createImageAsString(ecg2, "II"),
                                painter.createImageAsString(ecg3, "III"),
                                resp!!
                        )
                        try {
                            monitor.stopSendingData()
                        } catch (ignore: Exception) {
                            /**/
                        }
                        publisher.publishEvent(TaskComplete(task))
                    }
                }
            }
            valuePacketDataListener = monitor.addValueDataListener {
                heartRate = it.value.hr.toInt()
                resp = it.value.rspEcg.toInt()
                monitor.removeValueDataListener(it.currentListener)
                completeTaskIfReady()
            }
            wavePacketData3Listener = monitor.addWaveData3Listener {
                val value = it.value
                ecg1.add(value.ecgWave)
                ecg2.add(value.ecg2Wave)
                ecg3.add(value.ecg3Wave)
                if (ecg1.size == ECG_WAVES_COUNT) {
                    monitor.removeWaveData3Listener(it.currentListener)
                    completeTaskIfReady()
                }
            }

        } catch (e: Exception) {
            log.error("{}", e.message, e)
            publisher.publishEvent(TaskError(task))
            valuePacketDataListener?.let { monitor.removeValueDataListener(it) }
            wavePacketData3Listener?.let { monitor.removeWaveData3Listener(it) }
        }
    }


    companion object {
        const val ECG_WAVES_COUNT = 25
        private val log: Logger = LoggerFactory.getLogger(Electrocardiograph::class.java)
    }
}
