package ru.viscur.autotests.restApi

import ru.viscur.autotests.dto.TaskInfo
import ru.viscur.autotests.utils.Helpers

class DeviceRequests {

    companion object {

        fun addTask(taskType: String, desktopUid: String, payload: String? = null) =
                Helpers.createRequestSpec(mapOf("type" to taskType, "desktopId" to desktopUid, "payload" to payload)).`when`().
                        post(Endpoints.ADD_DEVICE_TASK).
                        then().statusCode(200).log().all().
                        extract().response().`as`(TaskInfo::class.java)

        fun getTaskStatus(taskId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.GET_TASK_STATUS + "/$taskId").
                        then().statusCode(200).log().all().
                        extract().response().`as`(TaskInfo::class.java)

        fun getTaskResult(taskId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_TASK_RESULT + "/$taskId").
                        then().statusCode(200).log().all()
    }
}
