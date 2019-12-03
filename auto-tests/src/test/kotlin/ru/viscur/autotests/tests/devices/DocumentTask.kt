package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.restApi.Endpoints

@Disabled("Debug purposes only")
class DocumentTask {

    @Test
    fun addDocumentTask() {
        //создание Document task
        val responseDocument = DeviceRequests.addTask(
                taskType = "Document",
                desktopUid = Endpoints.TEST_UID
        )

        //проверка ответа
        assertNotNull(responseDocument.id, "task id is null")
        assertNotNull(responseDocument.desktopId, "desktopId is null")
        assertEquals("Document",responseDocument.type, "wrong task type")
    }

    @Test
    fun getDocumentTaskStatus() {
        //создание Document task
        val responseDocument = DeviceRequests.addTask(
            taskType = "Document",
            desktopUid = Endpoints.TEST_UID
        )
        val taskId = responseDocument.id!!
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus.status)
    }

    @Test
    fun getDocumentTaskResult() {

    }
}
