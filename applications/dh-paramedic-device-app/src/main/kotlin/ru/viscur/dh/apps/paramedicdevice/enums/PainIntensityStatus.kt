package ru.viscur.dh.apps.paramedicdevice.enums

/**
 * Статус ответа приложения для монитора шкалы боли
 *
 * @param Ok Успешно
 * @param NoData Успешно, но данные отсутсвуют
 * @param Error Ошибка
 *
 * @see "https://gitlab.com/digital-hospital/pain-intensity-monitor"
 */
enum class PainIntensityStatus {
    Ok,
    NoData,
    Error
}