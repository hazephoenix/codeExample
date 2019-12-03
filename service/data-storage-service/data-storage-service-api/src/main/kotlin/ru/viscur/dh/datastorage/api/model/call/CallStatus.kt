package ru.viscur.dh.datastorage.api.model.call

enum class CallStatus {
    /**
     * Ожидает ответа
     */
    Awaiting,

    /**
     * Вызов принят
     */
    Accepted,

    /**
     * Отказ
     */
    Declined
}