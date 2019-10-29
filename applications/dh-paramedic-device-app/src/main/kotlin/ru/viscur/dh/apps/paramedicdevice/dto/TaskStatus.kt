package ru.viscur.dh.apps.paramedicdevice.dto

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
    Error
}
