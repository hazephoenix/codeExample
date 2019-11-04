package ru.viscur.autotests.dto

import ru.viscur.dh.fhir.model.enums.PatientQueueStatus


/**
 * Описание элементов в очереди одного кабинета
 *
 * @param officeId id офиса
 */
data class QueueItemsOfOffice  (
        val officeId: String,
        val items: List<QueueItemInfo>
)

/**
 * Created at 04.11.2019 10:44 by SherbakovaMA
 *
 * Описание элемента в очереди
 *
 * @param patientId id пациента
 * @param status статус пациента в очереди
 */
data class QueueItemInfo(
        val patientId: String,
        val status: PatientQueueStatus = PatientQueueStatus.IN_QUEUE
)