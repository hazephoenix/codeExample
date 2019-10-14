package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 9:08 by SherbakovaMA
 *
 * Статус измерение/назначенное [обследование Observation][ru.viscur.dh.fhir.model.entity.Observation]
 * [info](http://fhir-ru.github.io/valueset-observation-status.html)
 */
enum class ObservationStatus {
    /**
     * Зарегестрировано, не сделано
     */
    registered,
    /**
     * Не завершено (черновик)
     */
    preliminary,
    /**
     * Полностью завершено
     */
    final,
    /**
     * Изменен. После [final] изменено/обновлено/внесены правки
     */
    amended,
    /**
     * Исправлен. После [final] исправлена ошибка в данных. Частный случай (подтип) [amended]
     */
    corrected,
    /**
     * Отменено/прервано
     */
    cancelled,
    /**
     * Введен по ошибке
     */
    entered_in_error,
    /**
     * Не определен/не известен
     */
    unknown
}