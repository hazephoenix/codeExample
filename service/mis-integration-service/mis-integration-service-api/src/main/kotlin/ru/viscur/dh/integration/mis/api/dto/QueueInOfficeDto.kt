package ru.viscur.dh.integration.mis.api.dto

/**
 * Created at 12.11.2019 18:06 by SherbakovaMA
 *
 * Описание очереди в кабинет/к врачу
 *
 * @param practitioner информация о мед. персонале в кабинете (если требуется по мед. персоналу информация)
 * @param officeId id кабинета
 * @param queueSize количество пациентов в очереди
 * @param queueWaitingSum ожидание в очереди (предп. время обслуживание всех пациентов в очереди)
 * @param queueWorkload нагрузка (сумма "весов" каждого пациента в очереди)
 * @param items описание элементов очереди, [QueueItemDto]
 */
data class QueueInOfficeDto(
        var practitioner: PractitionerDto? = null,
        val officeId: String? = null,
        val queueSize: Int,
        val queueWaitingSum: Int,
        val queueWorkload: Int,
        val items: List<QueueItemDto>
)