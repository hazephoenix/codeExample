package ru.viscur.dh.apps.paramedicdevice.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.http.HttpStatus
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.apps.paramedicdevice.dto.TemperatureResponse
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType

/**
 * Температура теля
 */
@Component
@Profile("!fake-device")
class Temperature(
        private val restTemplate: RestTemplate,
        @Value("\${paramedic.native-service.url:http://localhost:8850}")
        private val nativeServiceUrl: String,
        private val publisher: ApplicationEventPublisher,
        private val executor: ThreadPoolTaskExecutor
) {
    private val log: Logger = LoggerFactory.getLogger(Temperature::class.java)


    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Temperature) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    private fun doMeasure(task: Task) {
        val response = restTemplate
                .getForEntity<ResponseWrapper>("$nativeServiceUrl/fora/ir20b/read-value", ResponseWrapper::class.java)
        if (response.statusCode == HttpStatus.OK) {
            executor.run {
                var running = true
                while (running) {
                    val response = restTemplate
                            .getForEntity<ResponseWrapper>("$nativeServiceUrl/fora/ir20b/check-result", ResponseWrapper::class.java)
                    val resp = response.body
                    if (resp == null) {
                        running = false
                        publisher.publishEvent(TaskError(task))
                    } else {
                        running = handleCheckResponse(resp, task)
                    }
                }
            }
        } else {
            publisher.publishEvent(TaskError(task))
        }
    }

    private fun handleCheckResponse(resp: ResponseWrapper, task: Task): Boolean {
        var repeat = false
        when (resp.status) {
            Status.Processing -> {
                repeat = true
                Thread.sleep(1000)
            }
            Status.Ok -> {
                task.result = resp.value
                publisher.publishEvent(TaskComplete(task))
            }
            else -> {
                log.error("Error status {}: {}", resp.status, resp.error)
                publisher.publishEvent(TaskError(task))
            }
        }
        return repeat
    }


    data class ResponseWrapper(
            val status: Status,
            val value: TemperatureResponse?,
            val error: String?
    )

    enum class Status {

        /**
         * Изначальное состояние, чтение значения еще не разу не запускалось
         */
        None,
        /**
         * Значение получено и включено в результат
         */
        Ok,

        /**
         * В процессе обработки
         */
        Processing,

        /**
         * Не дождались устройства
         */
        DeviceTimeout,

        /**
         * Не дождались значение
         */
        ValueTimeout,

        /**
         * Произошла ошибка выполнения
         */
        Error
    }
}
