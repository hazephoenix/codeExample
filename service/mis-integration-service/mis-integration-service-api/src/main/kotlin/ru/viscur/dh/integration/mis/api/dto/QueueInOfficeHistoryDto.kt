package ru.viscur.dh.integration.mis.api.dto

/**
 * Created at 12.11.2019 18:08 by SherbakovaMA
 *
 * Информация об очереди в кабинет за заданный период
 *
 * @param officeId
 * @param queueItemsSize количество пройденных пациентов в очереди
 * @param workload нагрузка (сумма "весов" каждого пациента в очереди)
 */
data class QueueInOfficeHistoryDto(
        val officeId: String,
        val queueItemsSize: Int,
        val workload: Int
)