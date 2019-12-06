package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.utils.Constants.Companion.HEIGHT_TASK
import ru.viscur.autotests.utils.Constants.Companion.TEST_DESKTOP_UID

@Disabled("Debug purposes only")
class HeightTask {

    companion object {
        val taskType = HEIGHT_TASK
        val desktopUid = TEST_DESKTOP_UID
    }

    @Test
    fun addHeightTask() {
        //создание Document task

        val responseHeight = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )

        //проверка ответа
        assertNotNull(responseHeight.id, "task id is null")
        assertNotNull(responseHeight.desktopId, "desktopId is null")
        assertEquals(taskType, responseHeight.type, "wrong task type")
    }

    @Test
    fun getHeightTaskStatus() {
        //создание Document task
        val responseHeight = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )
        val taskId = responseHeight.id
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus)
    }

    @Test
    fun getHeightTaskResult() {

    }
}