package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.utils.Constants.Companion.ELETROCARDIOGRAPH_TASK
import ru.viscur.autotests.utils.Constants.Companion.TEST_DESKTOP_UID

//@Disabled("Debug purposes only")
class ElectrocardiographTask {

    companion object {
        val taskType = ELETROCARDIOGRAPH_TASK
        val desktopUid = TEST_DESKTOP_UID
    }

    @Test
    fun addElectrocardiographTask() {
        //создание Document task

        val responseElectrocardiograph = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )

        //проверка ответа
        assertNotNull(responseElectrocardiograph.id, "task id is null")
        assertNotNull(responseElectrocardiograph.desktopId, "desktopId is null")
        assertEquals(taskType, responseElectrocardiograph.type, "wrong task type")
    }

    @Test
    fun getElectrocardiographTaskStatus() {
        //создание Document task
        val responseElectrocardiograph = DeviceRequests.addTask(
            taskType = taskType,
            desktopUid = desktopUid
        )
        val taskId = responseElectrocardiograph.id!!
        //получение статуса Document task
        val responseTaskStatus = DeviceRequests.getTaskStatus(taskId)

        //проверка ответа
        assertNotNull(responseTaskStatus)
    }

    @Test
    fun getElectrocardiographTaskResult() {

    }
}