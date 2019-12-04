package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.utils.Constants.Companion.TEMPERATURE_TASK
import ru.viscur.autotests.utils.Constants.Companion.TEST_DESKTOP_UID

@Disabled("Debug purposes only")
class TemperatureTask {

    companion object {
        val taskType = TEMPERATURE_TASK
        val desktopUid = TEST_DESKTOP_UID
    }

    @Test
    fun addTemperatureTask() {
        //создание Document task

        val responseTemperature = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )

        //проверка ответа
        assertNotNull(responseTemperature.id, "task id is null")
        assertNotNull(responseTemperature.desktopId, "desktopId is null")
        assertEquals(taskType, responseTemperature.type, "wrong task type")
    }

    @Test
    fun getTemperatureTaskStatus() {
        //создание Document task
        val responseTemperature = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )
        val taskId = responseTemperature.id!!
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus)
    }

    @Test
    fun getTemperatureTaskResult() {

    }
}