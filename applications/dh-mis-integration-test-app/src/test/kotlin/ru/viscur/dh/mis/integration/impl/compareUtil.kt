package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Assertions.assertEquals
import ru.viscur.dh.apps.misintegrationtest.util.QueueOfOfficeSimple
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Created at 08.11.2019 14:19 by SherbakovaMA
 *
 * Функции сравнения для тестов
 */

fun checkQueueItems(itemsByOffices: List<QueueOfOfficeSimple>, actQueueItems: List<QueueItem>) {
    val allItems = itemsByOffices.flatMap { it.items }
    val itemsStr = itemsToStr(itemsByOffices, actQueueItems)
    //количество в принципе разное
    assertEquals(allItems.size, actQueueItems.size, "wrong number of queueItems. $itemsStr")
    itemsByOffices.forEach { byOffice ->
        val officeId = byOffice.officeId
        byOffice.items.forEachIndexed { index, queueItemInfo ->
            //поиск соответствующего элемента в текущих
            val foundInAct = actQueueItems.filter { it.subject.id == queueItemInfo.patientId && it.location.id == officeId }
            assertEquals(1, foundInAct.size, "not found (or found multiple items) of $queueItemInfo. $itemsStr")
            val foundItem = foundInAct.first()
            //проверка правильности данных в найденном
            assertEquals(index, foundItem.onum, "wrong onum of $queueItemInfo. $itemsStr")
        }
    }
}
private fun itemsToStr(itemsByOffices: List<QueueOfOfficeSimple>, actQueueItems: List<QueueItem>): String {
    val actByOffices = actQueueItems.groupBy { it.location.id!! }
    return "\n\nexp queue:\n" +
            itemsByOffices.joinToString("\n") { byOffice -> byOffice.officeId + ":\n  " + byOffice.items.mapIndexed { index, queueItemInfo -> "$index. $queueItemInfo" }.joinToString("\n  ") } +
            "\n\nactual queue:\n" +
            actByOffices.map { (officeId, items) ->
                officeId + "\n  " + items.sortedBy { it.onum }.joinToString("\n  ")
            }.joinToString("\n  ") +
            "\n\n"
}