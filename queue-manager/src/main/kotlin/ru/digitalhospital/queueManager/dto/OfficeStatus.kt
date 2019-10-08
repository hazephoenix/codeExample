package ru.digitalhospital.queueManager.dto

/**
 * Created at 03.09.2019 12:44 by SherbakovaMA
 *
 * Статус кабинета
 */
enum class OfficeStatus {
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