package ru.viscur.dh.apps.paramedicdevice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import ru.viscur.dh.apps.paramedicdevice.configuration.AppUID
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskStatus

@SpringBootApplication
class DeviceMonitorApplication(
        private val publisher: ApplicationEventPublisher,
        private val uuid: AppUID,
        private val jmsTemplate: JmsTemplate
) {

    /**
     * Слушаем запросы на выполнение задач
     */
    @JmsListener(destination = "paramedic-requested-tasks")
    fun jmsListener(task: Task) {
        if (task.desktopId == uuid.uid) {
            publisher.publishEvent(TaskRequested(task))
        }
    }

    @EventListener(TaskStarted::class)
    fun taskStarted(event: TaskStarted) {
        event.task.status = TaskStatus.InProgress
        jmsTemplate.convertAndSend("paramedic-tasks-change-status", event.task)
    }

    @EventListener(TaskComplete::class)
    fun taskComplete(event: TaskComplete) {
        event.task.status = TaskStatus.Complete
        jmsTemplate.convertAndSend("paramedic-tasks-change-status", event.task)
    }

    @EventListener(TaskError::class)
    fun taskComplete(event: TaskError) {
        event.task.status = TaskStatus.Error
        jmsTemplate.convertAndSend("paramedic-tasks-change-status", event.task)
    }
}

fun main(args: Array<String>) {
    runApplication<DeviceMonitorApplication>(*args)
}
