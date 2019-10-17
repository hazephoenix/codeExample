package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus

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

    /**
     * Удаление всех записей [QueueItem]
     */
    fun deleteQueueItems()

    /**
     * Стоит ли пациент в очереди к какому-нибудь кабинету. Если да, то возвращается найденный кабинет
     */
    fun isPatientInOfficeQueue(patientId: String): String?

    fun involvedOffices(): List<Location>

    fun involvedPatients(): List<Patient>
}