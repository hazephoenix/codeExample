package ru.viscur.dh.common.dto.task

import java.util.*

/**
 * Created at 23.10.2019 12:30 by TimochkinEA
 *
 * Задача для АРМ фельдшера
 *
 * @property id         id задачи (UUID)
 * @property desktopId  id АРМ фельдшера, на котором надо выполнить задачу
 * @property type       тип задачи, см. [TaskType]
 * @property status     текущий статус задачи, см. [TaskStatus]
 * @property result     результат выполнения задачи
 * @property payload    полезная нагрузка для задачи (параметры и всё такое)
 */
data class Task(
        val id: String = UUID.randomUUID().toString(),
        val desktopId: String = "",
        val type: TaskType = TaskType.Unknown,
        var status: TaskStatus = TaskStatus.Await,
        var result: Any? = null,
        var payload: Map<String, Any>? = null
)
