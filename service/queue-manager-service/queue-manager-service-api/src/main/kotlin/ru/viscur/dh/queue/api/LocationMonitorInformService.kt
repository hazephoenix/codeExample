package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.dto.LocationMonitorDto

/**
 * Created at 19.11.2019 11:33 by SherbakovaMA
 *
 * Сервис информирования об изменениях состояния очереди в кабинет для отображения на мониторах при кабинетах
 */
interface LocationMonitorInformService {

    /**
     * Очередь изменилась для указанных кабинетов
     * Необходимо проинформировать отображение состояния очереди
     */
    fun queueChanged(officeIds: List<String>)

    /**
     * Текущее состояние очереди
     */
    fun queueCurrentState(officeId: String): LocationMonitorDto
}