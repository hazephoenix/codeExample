package ru.viscur.dh.fhir.model.enums

/**
 * Created at 02.10.2019 18:23 by SherbakovaMA
 *
 * Статус места (кабинета) [ru.viscur.dh.fhir.model.entity.Location]
 */
enum class LocationStatus {
    /**
     * Открыт
     */
    active,
    /**
     * Приостановлен
     */
    suspended,
    /**
     * Закрыт
     */
    inactive
}
