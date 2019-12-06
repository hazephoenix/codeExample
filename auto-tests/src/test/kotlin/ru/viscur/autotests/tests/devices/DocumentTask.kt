package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.utils.Constants.Companion.DOCUMENT_TASK
import ru.viscur.autotests.utils.Constants.Companion.TEST_DESKTOP_UID

@Disabled("Debug purposes only")
class DocumentTask {

    companion object {
        val taskType = DOCUMENT_TASK
        val desktopUid = TEST_DESKTOP_UID
    }

    @Test
    fun addDocumentTask() {
        //создание Document task

        val responseDocument = DeviceRequests.addTask(
                taskType = taskType,
                desktopUid = desktopUid
        )

        //проверка ответа
        assertNotNull(responseDocument.id, "task id is null")
        assertNotNull(responseDocument.desktopId, "desktopId is null")
        assertEquals(taskType,responseDocument.type, "wrong task type")
    }

    @Test
    fun getDocumentTaskStatus() {
        //создание Document task
        val responseDocument = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )
        val taskId = responseDocument.id!!
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus)
    }

    @Test
    fun getDocumentTaskResult() {

    }
}
