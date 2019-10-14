package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 10:27 by SherbakovaMA
 *
 * Статус [плана назначений пациенту CarePlan][ru.viscur.dh.fhir.model.entity.CarePlan]
 */
enum class CarePlanStatus {
    draft,
    /**
     * Проходит обследования
     */
    active,
    /**
     * Преостановлено (вне очереди)
     */
    suspended,
    /**
     * Прошел обследования, ждет результаты
     */
    waiting_results,
    /**
     * Результаты готовы
     */
    results_are_ready,
    /**
     * Завершен
     */
    completed,
    entered_in_error,
    cancelled,
    unknown
}