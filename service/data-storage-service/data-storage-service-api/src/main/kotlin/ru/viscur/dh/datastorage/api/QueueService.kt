package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.entity.QueueHistoryOfPatient
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import java.util.*

/**
 * Created at 16.10.2019 12:13 by SherbakovaMA
 *
 * Сервис для работы с ресурсами, относящимся к очереди
 */
interface QueueService {

    /**
     * Все записи [QueueItem] по кабинетам [officeIds] с сортировкой по [QueueItem.onum]
     */
    fun queueItemsOfOffices(officeIds: List<String>): MutableList<QueueItem>

    /**
     * Все записи [QueueItem] по кабинету [officeId] с сортировкой по [QueueItem.onum]
     */
    fun queueItemsOfOffice(officeId: String): MutableList<QueueItem> = queueItemsOfOffices(listOf(officeId))

    /**
     * Удаление всех записей [QueueItem] по кабинету [officeId]
     */
    fun deleteQueueItemsOfOffice(officeId: String)

    /**
     * Стоит ли пациент в очереди к какому-нибудь кабинету. Если да, то возвращается найденный кабинет
     */
    fun isPatientInOfficeQueue(patientId: String): String?

    /**
     * "Вовлеченные в очередь кабинеты": все кабинеты, указанные в [QueueItem]
     */
    fun involvedOffices(): List<Location>

    /**
     * "Вовлеченные в очередь пациенты": все пациенты, указанные в [QueueItem]
     */
    fun involvedPatients(): List<Patient>

    /**
     * Все [QueueItem]
     */
    fun queueItems(): List<QueueItem>

    /**
     * История проведения пациента в очереди за период
     */
    fun queueHistoryOfPatient(patientId: String, periodStart: Date, periodEnd: Date): List<QueueHistoryOfPatient>

    /**
     * История очереди за период
     * Выборка только со статусом [PatientQueueStatus.IN_QUEUE]
     * В результат попадают интервалы, имеющие пересечения с заданным периодом или полностью находятся в периоде
     */
    fun queueHistoryByPeriod(periodStart: Date, periodEnd: Date): List<QueueHistoryOfPatient>
}