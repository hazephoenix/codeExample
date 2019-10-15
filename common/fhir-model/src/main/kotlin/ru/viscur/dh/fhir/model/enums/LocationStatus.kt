package ru.viscur.dh.fhir.model.enums

/**
 * Created at 02.10.2019 18:23 by SherbakovaMA
 *
 * Статус места (кабинета) [ru.viscur.dh.fhir.model.entity.Location]
 */
enum class LocationStatus {
    /**
     * Закрыт (приема нет)
     */
    CLOSED,
    /**
     * Вообще кабинет ведет прием, но сейчас не может принять (по техн. причинам/врач отошел и т.д.)
     */
    BUSY,
    /**
     * Готов принять на осмотр пациента
     */
    READY,
    /**
     * Пациент из очереди назначен, ожидание прихода пациента
     */
    WAITING_USER,
    /**
     * Осмотр пациента
     */
    SURVEY
}
