package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.apps.paramedicdevice.dto.Task
import ru.viscur.dh.apps.paramedicdevice.dto.TaskType
import ru.viscur.dh.apps.paramedicdevice.dto.TvesResponse
import ru.viscur.dh.apps.paramedicdevice.events.TaskComplete
import ru.viscur.dh.apps.paramedicdevice.events.TaskError
import ru.viscur.dh.apps.paramedicdevice.events.TaskRequested
import ru.viscur.dh.apps.paramedicdevice.events.TaskStarted
import java.lang.Exception

/**
 * Created at 11.10.2019 11:36 by TimochkinEA
 *
 * Весы
 */
@Component
class Weight(
        private val restTemplate: RestTemplate,
        @Value("\${paramedic.tves.url:http://localhost:1221/tves}")
        private val tvesUrl: String,
        private val publisher: ApplicationEventPublisher
) {

    private val log: Logger = LoggerFactory.getLogger(Weight::class.java)

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Weight) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    private fun doMeasure(task: Task) {
        try {
            task.result = restTemplate.getForEntity("${tvesUrl}/scale", TvesResponse::class.java).body!!
            publisher.publishEvent(TaskComplete(task))
        } catch (e: Exception) {
            log.error("Error to take weight! ${e.message}", e)
            publisher.publishEvent(TaskError(task))
        }
    }
}
