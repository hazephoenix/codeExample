package ru.viscur.dh.common.dto.task

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
     * Печать браслета
     */
    Wristband,

    /**
     * Неизвестный тип
     */
    Unknown,
    /**
     * Измерения тонометра (АД, ЧСС)
     */
    Tonometer
}
