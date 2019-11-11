package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.apps.paramedicdevice.dto.TvesResponse
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType

/**
 * Created at 11.10.2019 12:48 by TimochkinEA
 *
 * Ростомер
 */
@Component
class Height(
        private val restTemplate: RestTemplate,
        @Value("\${paramedic.tves.url:http://localhost:1221/tves}")
        private val tvesUrl: String,
        private val publisher: ApplicationEventPublisher
){

    private val log: Logger = LoggerFactory.getLogger(Height::class.java)

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Height) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    /**
     * Выполнить измерение
     */
    private fun doMeasure(task: Task) {
        try {
            task.result = takeTvesResponse()
            publisher.publishEvent(TaskComplete(task))
        } catch (e: Exception) {
            log.error("Error while take height!: ${e.message}", e)
            publisher.publishEvent(TaskError(task))
        }
    }

    /**
     * Получить результат с устройства
     */
    private fun takeTvesResponse(): TvesResponse {
        restTemplate.postForLocation("${tvesUrl}/height/clear", "")
        restTemplate.postForLocation("${tvesUrl}/height/start", "")
        val res = restTemplate.getForEntity("${tvesUrl}/height", TvesResponse::class.java)
        return res.body!!
    }
}
