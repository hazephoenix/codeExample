package ru.viscur.dh.practitioner.call.model

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