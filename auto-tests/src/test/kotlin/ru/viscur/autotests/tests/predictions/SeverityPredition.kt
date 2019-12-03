package ru.viscur.autotests.tests.predictions

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers

@Disabled("Debug purposes only")
class SeverityPredition {

    @Test
    fun predictRedSeverity() {
        //3 критерия для выбора severity: Red, Yellow, Green
        val listSeverityCriteria = listOf(
            "Airways_not_passable_(asphyxia)_or_not_breathing", //Red criteria
            "Stun", //Yellow criteria
            "Can_Stand" //Green criteria
        )
        val bundle = Helpers.bundleForSeverity(listSeverityCriteria)

        //получение предположительной степени тяжести
        val severityResponse = QueRequests.getSeverity(bundle, "2")

        //проверка, что предполагается Red, т.к. один из критериев относится к RED
        severityResponse.assertThat().body("severity.code", equalTo("RED"))
    }

    @Test
    fun predictYellowSeverity() {
        //3 критерия для выбора severity: Yellow, Green, Green
        val listSeverityCriteria = listOf(
            "Airways_passable", //Green criteria
            "Stun", //Yellow criteria
            "Can_Stand" //Green criteria
        )
        val bundle = Helpers.bundleForSeverity(listSeverityCriteria)

        //получение предположительной степени тяжести
        val severityResponse = QueRequests.getSeverity(bundle, "2")

        //проверка, что предполагается Yellow, т.к. один из критериев относится к Yellow и отстутвуют Red
        severityResponse.assertThat().body("severity.code", equalTo("YELLOW"))
    }

    @Test
    fun predictGreenSeverity() {
        //3 критерия для выбора severity: Yellow, Green, Green
        val listSeverityCriteria = listOf(
            "Airways_passable", //Green criteria
            "Clear_Mind", //Green criteria
            "Can_Stand" //Green criteria
        )
        val bundle = Helpers.bundleForSeverity(listSeverityCriteria)

        //получение предположительной степени тяжести
        val severityResponse = QueRequests.getSeverity(bundle, "2")

        //проверка, что предполагается Yellow, т.к. один из критериев относится к Yellow и отстутвуют Red
        severityResponse.assertThat().body("severity.code", equalTo("GREEN"))
    }
}