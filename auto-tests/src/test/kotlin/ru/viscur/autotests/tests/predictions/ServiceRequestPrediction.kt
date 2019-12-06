package ru.viscur.autotests.tests.predictions

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests

@Disabled("Debug purposes only")
class ServiceRequestPrediction {

    @Test
    fun predictServRequestsByDiagnosis () {
        //создание диагноза
        val diagnosisCode = "A16"
        val diagnosis = mapOf(
            "diagnosis" to diagnosisCode,
            "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
            "gender" to "male"
        )

        //получение предположительных Service Request по диагнозу
        val servRequestsList = QueRequests.getSupposedServRequests(diagnosis)

        //проверка количества предположительных Service Requests
        Assertions.assertEquals(18, servRequestsList.size, "wrong number of service requests for diagnosis: $diagnosisCode"
        )
    }
}