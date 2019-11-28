package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.dto.LocationMonitorDto

/**
 * Created at 19.11.2019 10:06 by SherbakovaMA
 *
 * Диспетчер для отслеживания изменений состояния очереди в кабинеты
 */
interface QueueStateDispatcher {

    /**
     * Добавить для информирования обновленное состояние для одного кабинета
     */
    fun add(locationMonitorDto: LocationMonitorDto)
}