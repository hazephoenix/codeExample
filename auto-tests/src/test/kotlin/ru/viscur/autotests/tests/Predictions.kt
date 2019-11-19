package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueReportInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers

//@Disabled("Debug purposes only")
class Predictions {

    @Test
    fun predictDiagnosis() {
        val bundle = Helpers.bundleForDiagnosis("RED")
        val diagnosisList = QueRequests.getDiagnosis(bundle, "2")
    }

    @Test
    fun predictSeverity() {
        val bundle = Helpers.bundleForDiagnosis("1211")
        val severityResponse = QueRequests.getSeverity(bundle)
        severityResponse.
                assertThat().body("severity.code", equalTo("GREEN"))
    }

}