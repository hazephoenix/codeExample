package ru.viscur.dh.common.dto.events

import org.springframework.context.ApplicationEvent
import ru.viscur.dh.common.dto.task.Task

/**
 * Created at 28.10.2019 11:28 by TimochkinEA
 */


/**
 * Запрос на выполение задачи
 */
data class TaskRequested(val task: Task): ApplicationEvent(task)

/**
 * Задача запущена
 */
data class TaskStarted(val task: Task): ApplicationEvent(task)

/**
 * Задача завершена успешно
 */
data class TaskComplete(val task: Task): ApplicationEvent(task)

/**
 * Ошибка при выполнении задачи
 */
data class TaskError(val task: Task): ApplicationEvent(task)
