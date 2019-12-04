package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.utils.Constants.Companion.TEST_DESKTOP_UID
import ru.viscur.autotests.utils.Constants.Companion.WEIGHT_TASK

@Disabled("Debug purposes only")
class WeightTask {

    companion object {
        val taskType = WEIGHT_TASK
        val desktopUid = TEST_DESKTOP_UID
    }

    @Test
    fun addWeightTask() {
        //создание Document task

        val responseWeight = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )

        //проверка ответа
        assertNotNull(responseWeight.id, "task id is null")
        assertNotNull(responseWeight.desktopId, "desktopId is null")
        assertEquals(taskType, responseWeight.type, "wrong task type")
    }

    @Test
    fun getWeightTaskStatus() {
        //создание Document task
        val responseWeight = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )
        val taskId = responseWeight.id!!
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus)
    }

    @Test
    fun getWeightTaskResult() {

    }
}