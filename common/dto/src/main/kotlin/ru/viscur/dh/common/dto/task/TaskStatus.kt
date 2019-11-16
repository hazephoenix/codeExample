package ru.viscur.dh.common.dto.task

/**
 * Created at 23.10.2019 12:31 by TimochkinEA
 *
 * Статус задачи
 */
enum class TaskStatus {
    /**
     * Ожидает выполнения
     */
    Await,

    /**
     * Выполняется
     */
    InProgress,

    /**
     * Выполнена успешно
     */
    Complete,

    /**
     * Ошибка выполнения
     */
    Error,

    /**
     * Превышено время выполнения задачи
     */
    TimedOut
}
