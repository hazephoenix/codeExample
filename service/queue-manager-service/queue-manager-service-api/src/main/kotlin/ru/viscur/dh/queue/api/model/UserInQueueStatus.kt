package ru.viscur.dh.queue.api.model

/**
 * Created at 03.09.2019 12:44 by SherbakovaMA
 *
 * Статус пациента в общей очереди
 */
enum class UserInQueueStatus {
    /**
     * Готов встать в очередь / свободен
     */
    READY,
    /**
     * В очереди в определенный кабинет
     */
    IN_QUEUE,
    /**
     * Наступила его очередь в опр кабинет, идет в кабинет на обсл-е
     */
    GOING_TO_SURVEY,
    /**
     * На осмотре в кабинете
     */
    ON_SURVEY,
    /**
     * Закончил все обследования в маршрутном листе
     */
    FINISHED
}