package ru.viscur.dh.fhir.model.enums

/**
 * Created at 02.10.2019 18:55 by SherbakovaMA
 *
 * Статус помещения(местоположения) взаимодействия [EncounterLocation]
 */
enum class EncounterLocationStatus {
    /**
     * Запланированное
     */
    planned,
    /**
     * Текущее
     */
    active,
    /**
     * Зарезервированное
     */
    reserved,
    /**
     * Завершено ("пройдено", посещено)
     */
    completed
}