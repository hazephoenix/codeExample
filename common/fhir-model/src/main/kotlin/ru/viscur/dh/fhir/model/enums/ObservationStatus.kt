package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 9:08 by SherbakovaMA
 *
 * Статус измерение/назначенное [обследование Observation][ru.viscur.dh.fhir.model.entity.Observation]
 * [info](http://fhir-ru.github.io/valueset-observation-status.html)
 */
enum class ObservationStatus {
    /**
     * Зарегестрировано, не сделано (ожидаются результаты)
     */
    registered,
    /**
     * Полностью завершено
     */
    final,
    /**
     * Отменено/прервано
     */
    cancelled
}