package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers

@Disabled("Debug purposes only")
class Predictions {

    @Test
    fun predictDiagnosis() {
        val expectedDiagnosis = "A00"
        val bundle = Helpers.bundleForDiagnosis("RED")
        //получени и проверка предположительных диагнозов по результатам осмотра фельдшером
        val diagnosisList = QueRequests.getDiagnosis(bundle, "2")
        assertEquals(2, diagnosisList.diagnoses.size, "wrong diagnosis count")
        assertEquals(expectedDiagnosis, diagnosisList.diagnoses.first().code, "wrong diagnosis")
    }

    @Test
    fun predictSeverity() {
        val bundle = Helpers.bundleForSeverity()
        //получение и проверка степени тяжести пациента по результатам осмотра фельдшером
        val severityResponse = QueRequests.getSeverity(bundle)
        severityResponse.
                assertThat().body("severity.code", equalTo("GREEN"))
    }
}