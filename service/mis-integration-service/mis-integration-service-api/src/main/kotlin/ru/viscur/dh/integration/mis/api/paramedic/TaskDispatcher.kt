package ru.viscur.dh.integration.mis.api.paramedic

import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskStatus

/**
 * Created at 09.11.2019 15:04 by TimochkinEA
 *
 * Диспетчер для отслеживания задач
 */
interface TaskDispatcher {

    /**
     * Добавить задачу на исполение
     * @param task описание задачи
     */
    fun add(task: Task): Task

    /**
     * Статус задачи
     * @param id    ID задачи
     */
    fun status(id: String): TaskStatus

    /**
     * Результат выполнения задачи
     * @param id    ID задачи
     */
    fun result(id: String): Task
}
