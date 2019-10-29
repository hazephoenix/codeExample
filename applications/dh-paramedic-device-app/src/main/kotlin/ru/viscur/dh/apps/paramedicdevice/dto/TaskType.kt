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
    Documents,

    /**
     * Измерение веса
     */
    Weight,

    /**
     * Измерение роста
     */
    Height,

    /**
     * Неизвестный тип
     */
    Unknown
}
