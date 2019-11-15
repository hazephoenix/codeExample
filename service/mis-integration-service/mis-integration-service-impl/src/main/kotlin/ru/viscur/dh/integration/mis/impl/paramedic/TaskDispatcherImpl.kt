package ru.viscur.dh.integration.mis.impl.paramedic

import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskStatus
import ru.viscur.dh.integration.mis.api.paramedic.TaskDispatcher
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Created at 28.10.2019 11:25 by TimochkinEA
 *
 * Диспетчер задач (лол) для АРМ фельдшера
 */
@Service
class TaskDispatcherImpl(
        private val jmsTemplate: JmsTemplate
): TaskDispatcher {

    private val tasks: MutableMap<String, Task> = ConcurrentHashMap()

    override fun add(task: Task): Task {
        tasks[task.id] = task
        jmsTemplate.convertAndSend("paramedic-requested-tasks", task)
        return task
    }

    override fun status(id: String) = tasks[id]!!.status

    override fun result(id: String) = tasks[id]!!

    /**
     * Обработка запуска задачи
     */
    @JmsListener(destination = "paramedic-tasks-change-status")
    fun listenTaskChanges(task: Task) {
        tasks[task.id] = task
    }


    /**
     * Удаление выполненных и выполненных с ошибкой задач
     */
    @Scheduled(fixedDelay = 600_000L)
    private fun cleanCompletedTasks() {
        val completedAndError = tasks.filterValues { it.status in arrayOf(TaskStatus.Complete, TaskStatus.Error, TaskStatus.TimedOut) }
        completedAndError.forEach{ (k, _) -> tasks.remove(k) }
    }

    /**
     * Проверяем не истекло ли время исполнения задачи
     */
    @Scheduled(fixedDelay = 3000L)
    private fun checkTaskTTL() {
        val notCompletedTasks = tasks.filterValues { it.status in arrayOf(TaskStatus.Await, TaskStatus.InProgress) }
        notCompletedTasks.forEach { (k, v) ->
            if ((v.addedTime + v.ttl) > LocalDateTime.now()) {
                tasks[k]!!.status = TaskStatus.TimedOut
            }
        }
    }
}
