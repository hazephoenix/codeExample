package ru.viscur.dh.apps.paramedicdevice.dto

/**
 * Created at 28.10.2019 11:30 by TimochkinEA
 *
 * Типы задач
 */
enum class TaskType {
    /**
     * Сканирование документов
     */
    Document,

    /**
     * Измерение веса
     */
    Weight,

    /**
     * Измерение роста
     */
    Height,

    /**
     * Температура
     */
    Temperature,

    /**
     * Неизвестный тип
     */
    Unknown
}
