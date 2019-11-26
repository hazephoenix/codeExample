package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.*
import org.springframework.context.annotation.*
import org.springframework.context.event.*
import org.springframework.http.*
import org.springframework.scheduling.concurrent.*
import org.springframework.stereotype.*
import org.springframework.web.client.*
import ru.viscur.dh.apps.paramedicdevice.dto.*
import ru.viscur.dh.apps.paramedicdevice.enums.*
import ru.viscur.dh.common.dto.events.*
import ru.viscur.dh.common.dto.task.*


/**
 * Монитор шкалы интенсивности боли
 */
@Component
@Profile("!fake-device")
class PainIntensityMonitor(
        private val restTemplate: RestTemplate,
        @Value("\${paramedic.native-service.url:http://localhost:8851}")
        private val nativeServiceUrl: String,
        private val publisher: ApplicationEventPublisher,
        private val executor: ThreadPoolTaskExecutor
) {
    private val log: Logger = LoggerFactory.getLogger(Temperature::class.java)

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.PainIntensity) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    fun doMeasure(task: Task) {
        val response = restTemplate
                .getForEntity<PainIntensityResponseWrapper>("$nativeServiceUrl/pain-intensity-monitor/scale")
        if (response.statusCode == HttpStatus.OK) {
            executor.run {
                var running = true
                while (running) {
                    val response = restTemplate
                            .getForEntity("$nativeServiceUrl/pain-intensity-monitor/result", PainIntensityResponseWrapper::class.java)
                    val resp = response.body
                    if (resp == null) {
                        running = false
                        publisher.publishEvent(TaskError(task))
                    } else {
                        running = handleCheckResponse(resp, task)
                    }
                }
            }
            publisher.publishEvent(TaskComplete(task))
        } else  {
            publisher.publishEvent(TaskError(task))
        }
    }

    private fun handleCheckResponse(resp: PainIntensityResponseWrapper, task: Task): Boolean {
        var repeatRequest = false
        when (resp.status) {
            PainIntensityStatus.NoData -> {
                repeatRequest = true
                Thread.sleep(1000)
            }
            PainIntensityStatus.Ok -> {
                task.result = resp.value
                publisher.publishEvent(TaskComplete(task))
            }
            else -> {
                log.error("Error status {}", resp.status)
                publisher.publishEvent(TaskError(task))
            }
        }
        return repeatRequest
    }
}
