package ru.viscur.autotests.tests

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers

@Disabled("Debug purposes only")
class Diagnosis {

    @Test
    fun gettingDiagnosis() {
        val bundle = Helpers.bundleForDiagnosis("1211")
        val diagnosisList = QueRequests.getDiagnosis(bundle, "2")
    }

    @Test
    fun gettingSeverity() {
        val bundle = Helpers.bundleForDiagnosis("1211")
        val severity = QueRequests.getSeverity(bundle)
    }
}