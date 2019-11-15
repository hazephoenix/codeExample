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
import ru.viscur.dh.apps.paramedicdevice.dto.DocumentResponse
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType

/**
 * Сканер документов
 */
@Component
@Profile("!fake-device")
class Document(
        private val restTemplate: RestTemplate,
        @Value("\${paramedic.native-service.url:http://localhost:8850}")
        private val nativeServiceUrl: String,
        private val publisher: ApplicationEventPublisher,
        private val executor: ThreadPoolTaskExecutor
) {
    private val log: Logger = LoggerFactory.getLogger(Document::class.java)

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Document) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    private fun doMeasure(task: Task) {
        val scanResponse = restTemplate
                .getForEntity("$nativeServiceUrl/fora/document-reader/scan", ScanResponse::class.java)
        if (scanResponse.statusCode == HttpStatus.OK && !scanResponse.body?.uuid.isNullOrBlank()) {
            val checkUrl = "$nativeServiceUrl/document-reader/check-result?request-uuid=${scanResponse.body!!.uuid}"
            executor.run {
                var running = true
                while (running) {
                    val response = restTemplate.getForEntity(checkUrl, ResponseWrapper::class.java)
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
        var repeatRequest = false
        when (resp.status) {
            Status.InProgress -> {
                repeatRequest = true
                Thread.sleep(1000)
            }
            Status.Ok -> {
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


    data class ResponseWrapper(
            val status: Status,
            val value: DocumentResponse?
    )

    data class ScanResponse(
            val uuid: String
    )

    /**
     * Статус скагирования
     */
    enum class Status {
        /**
         * В процессе (ожидается документ, сканируется и т.д.)
         */
        InProgress,

        /**
         * Документ отсканирован, разобран, есть результат
         */
        Ok,

        /**
         * Ошибка разбора документа
         */
        DocumentError,

        /**
         * Сканирование было отменено до того как закончилось
         * (например, в случае, если запустили новое сканирование до того как окончилось текущее или явно вызвали abort)
         */
        Aborted,

        /**
         * Ошибка устройства (во время ожидания сканирования отвалился device)
         */
        DeviceError
    }

}
