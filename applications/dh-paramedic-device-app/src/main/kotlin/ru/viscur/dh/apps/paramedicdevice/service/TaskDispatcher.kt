package ru.viscur.dh.apps.paramedicdevice.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.viscur.dh.apps.paramedicdevice.dto.Task
import ru.viscur.dh.apps.paramedicdevice.dto.TaskStatus
import ru.viscur.dh.apps.paramedicdevice.events.TaskComplete
import ru.viscur.dh.apps.paramedicdevice.events.TaskError
import ru.viscur.dh.apps.paramedicdevice.events.TaskRequested
import ru.viscur.dh.apps.paramedicdevice.events.TaskStarted
import java.util.concurrent.ConcurrentHashMap

/**
 * Created at 28.10.2019 11:25 by TimochkinEA
 */
@Service
class TaskDispatcher(private val publisher: ApplicationEventPublisher) {

    private val tasks: MutableMap<String, Task> = ConcurrentHashMap()

    /**
     * Добавить задачу
     */
    fun add(task: Task) {
        tasks[task.id] = task
        publisher.publishEvent(TaskRequested(task))
    }

    /**
     * Обработка запуска задачи
     */
    @EventListener(TaskStarted::class)
    fun listenStartedTasks(event: TaskStarted) {
        val task = event.task
        tasks[task.id]?.status = TaskStatus.InProgress
    }

    /**
     * Обработка завершения задачи
     */
    @EventListener(TaskComplete::class)
    fun listenCompletedTasks(event: TaskComplete) {
        val task = event.task
        tasks[task.id]?.status = TaskStatus.Complete
    }

    /**
     * Обработка выполнения задачи с ошибкой
     */
    @EventListener
    fun listenErrorTask(event: TaskError) {
        val task = event.task
        tasks[task.id]?.status = TaskStatus.Error
    }

    /**
     * Текущий статус задачи
     */
    fun taskStatus(id: String) = tasks[id]?.status

    /**
     * Выполненная задача
     */
    fun taskResult(id: String) = tasks[id]

    /**
     * Удаление выполненных и выполненных с ошибкой задач
     */
    @Scheduled(fixedDelay = 60000L)
    fun cleanCompletedTasks() {
        val completedAndError = tasks.filterValues { it.status in arrayOf(TaskStatus.Complete, TaskStatus.Error) }
        completedAndError.forEach{ (k, _) -> tasks.remove(k) }
    }
}
