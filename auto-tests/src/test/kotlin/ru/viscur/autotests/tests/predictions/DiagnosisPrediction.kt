package ru.viscur.autotests.tests.predictions

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers

class DiagnosisPrediction {

    @Test
    fun predictDiagnosis() {
        val bundle = Helpers.bundleForDiagnosis()
        //получение и проверка предположительных диагнозов по результатам осмотра фельдшером
        val diagnosisList = QueRequests.getDiagnosis(bundle, "10")
        Assertions.assertEquals(10, diagnosisList.diagnoses.size, "wrong diagnosis number")
    }
}