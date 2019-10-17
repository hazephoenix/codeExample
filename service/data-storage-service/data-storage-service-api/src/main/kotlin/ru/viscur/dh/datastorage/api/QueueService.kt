package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.QueueItem

/**
 * Created at 16.10.2019 12:13 by SherbakovaMA
 *
 * Сервис для работы с ресурсами, относящимся к очереди
 */
interface QueueService {

    /**
     * Все записи [QueueItem] по кабинету [officeId] с сортировкой по [QueueItem.onum]
     */
    fun queueItemsOfOffice(officeId: String): List<QueueItem>

    /**
     * Удаление всех записей [QueueItem] по кабинету [officeId]
     */
    fun deleteQueueItemsOfOffice(officeId: String)
}