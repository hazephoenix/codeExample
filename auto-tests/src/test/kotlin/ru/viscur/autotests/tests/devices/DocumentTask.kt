package ru.viscur.autotests.tests.devices

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.DeviceRequests
import ru.viscur.autotests.restApi.Endpoints

@Disabled("Debug purposes only")
class DocumentTask {

    @Test
    fun addDocumentTask() {
        val responseDocument = DeviceRequests.addTask(
                taskType = "Document",
                desktopUid = Endpoints.TEST_UID,
                payload = null).
                log().all().extract().response()
    }
}