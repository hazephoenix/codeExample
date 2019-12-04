package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.utils.Constants.Companion.TEST_DESKTOP_UID
import ru.viscur.autotests.utils.Constants.Companion.TONOMETER_TASK

@Disabled("Debug purposes only")
class TonometerTask {

    companion object {
        val taskType = TONOMETER_TASK
        val desktopUid = TEST_DESKTOP_UID
    }

    @Test
    fun addDocumentTask() {
        //создание Document task

        val responseTonometer = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )

        //проверка ответа
        assertNotNull(responseTonometer.id, "task id is null")
        assertNotNull(responseTonometer.desktopId, "desktopId is null")
        assertEquals(taskType, responseTonometer.type, "wrong task type")
    }

    @Test
    fun getTonometerTaskStatus() {
        //создание Document task
        val responseTonometer = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )
        val taskId = responseTonometer.id!!
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus)
    }

    @Test
    fun getTonometerTaskResult() {

    }
}