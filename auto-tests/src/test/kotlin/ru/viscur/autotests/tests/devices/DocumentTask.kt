package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.restApi.Endpoints

//@Disabled("Debug purposes only")
class DocumentTask {

    @Test
    fun addDocumentTask() {
        val responseDocument = DeviceRequests.addTask(
                taskType = "Temperature",
                desktopUid = Endpoints.TEST_UID
        )
    }

    @Test
    fun getTaskStatus() {
        val responseStatus = DeviceRequests.getTaskStatus("a73afb43-bfd7-41e8-9bc9-d1d0ffd6e46b")
    }
}
