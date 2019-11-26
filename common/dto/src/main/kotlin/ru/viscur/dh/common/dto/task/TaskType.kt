package ru.viscur.dh.common.dto.task

/**
 * Created at 28.10.2019 11:30 by TimochkinEA
 *
 * Типы задач
 */
enum class TaskType {
    /**
     * Неизвестный тип
     */
    Unknown,
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
     * Измерения тонометра (АД, ЧСС)
     */
    Tonometer,

    /**
     * Снятие показаний с пульсоксиметра
     */
    Pulseoximeter,

    /**
     * Снятие показаний с ЭКГ
     */
    Electrocardiograph,

    /**
     * Печать маршрутного листа
     */
    PrintRoute
}
