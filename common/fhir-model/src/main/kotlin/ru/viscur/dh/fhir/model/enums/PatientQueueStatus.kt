package ru.viscur.dh.fhir.model.enums

/**
 * Created at 14.10.2019 16:28 by SherbakovaMA
 *
 * Статус пациента в общей очереди [ru.viscur.dh.fhir.model.entity.QueueItem]
 */
enum class PatientQueueStatus {
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
    GOING_TO_OBSERVATION,
    /**
     * На осмотре в кабинете
     */
    ON_OBSERVATION
}