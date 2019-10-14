package ru.viscur.dh.fhir.model.enums

/**
 * Created at 03.10.2019 8:24 by SherbakovaMA
 *
 * Назначение использоания адреса
 */
enum class AddressUse {
    /**
     * Дом (проживание)
     */
    home,
    /**
     * Работа
     */
    work,
    /**
     * Временное
     */
    temp,
    /**
     * Устаревшее
     */
    old,
    /**
     * Для счетов
     */
    billing
}
