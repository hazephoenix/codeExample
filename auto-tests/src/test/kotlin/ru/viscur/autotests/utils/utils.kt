package ru.viscur.autotests.utils

import org.junit.jupiter.api.Assertions
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Проверка состояния очереди
 */
fun checkQueueItems(itemsByOffices: List<QueueItemsOfOffice>) {
    val actQueueItems = QueRequests.queueItems()
    val allItems = itemsByOffices.flatMap { it.items }
    val itemsStr = itemsToStr(itemsByOffices, actQueueItems)
    //количество в принципе разное
    Assertions.assertEquals(allItems.size, actQueueItems.size, "wrong number of queueItems. $itemsStr")
    itemsByOffices.forEach { byOffice ->
        val officeId = byOffice.officeId
        byOffice.items.forEachIndexed { index, queueItemInfo ->
            //поиск соответствующего элемента в текущих
            val foundInAct = actQueueItems.filter { it.subject.id == queueItemInfo.patientId && it.location.id == officeId }
            Assertions.assertEquals(1, foundInAct.size, "not found (or found multiple items) of $queueItemInfo. $itemsStr")
            val foundItem = foundInAct.first()
            //проверка правильности данных в найденном
            Assertions.assertEquals(index, foundItem.onum, "wrong onum of $queueItemInfo. $itemsStr")
            val actPatient = QueRequests.resource(ResourceType.Patient, queueItemInfo.patientId)
            Assertions.assertEquals(queueItemInfo.status, actPatient.extension.queueStatus, "wrong status of patient with id ${queueItemInfo.patientId}. $itemsStr")
            //правильность статуса кабинета в зависимости от статуса первого пациента в очереди
            if (index == 0) {
                val actOffice = QueRequests.resource(ResourceType.Location, officeId)
                val expPatientStatus = when (actOffice.status) {
                    LocationStatus.CLOSED, LocationStatus.BUSY -> PatientQueueStatus.IN_QUEUE
                    LocationStatus.OBSERVATION -> PatientQueueStatus.ON_OBSERVATION
                    LocationStatus.WAITING_PATIENT -> PatientQueueStatus.GOING_TO_OBSERVATION
                    else -> throw Exception("wrong status '${actOffice.status}' of office with id ${actOffice.id}. must be one of (CLOSED, BUSY, OBSERVATION, WAITING_PATIENT)")
                }
                Assertions.assertEquals(expPatientStatus, actPatient.extension.queueStatus,
                        "not proper status of office ${actOffice.id} ($expPatientStatus) to first patient in queue status: ${actPatient.extension.queueStatus}")
            }
        }
    }
}

private fun itemsToStr(itemsByOffices: List<QueueItemsOfOffice>, actQueueItems: List<QueueItem>): String {
    actQueueItems.forEach {
        it.apply {
            val patient = QueRequests.resource(ResourceType.Patient, subject.id!!)
            patientQueueStatus = patient.extension.queueStatus
        }
    }
    val actByOffices = actQueueItems.groupBy { it.location.id!! }
    return "\n\nexp queue:\n" +
            itemsByOffices.joinToString("\n") { byOffice -> byOffice.officeId + ":\n  " + byOffice.items.mapIndexed { index, queueItemInfo -> "$index. $queueItemInfo" }.joinToString("\n  ") } +
            "\n\nactual queue:\n" +
            actByOffices.map { (officeId, items) ->
                val office = QueRequests.resource(ResourceType.Location, officeId)
                officeId + " (${office.status}):\n  " + items.sortedBy { it.onum }.joinToString("\n  ")}.joinToString("\n  ") +
            "\n\n"
}