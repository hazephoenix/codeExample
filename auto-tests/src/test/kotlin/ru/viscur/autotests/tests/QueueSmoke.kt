package ru.viscur.autotests.tests

import org.slf4j.LoggerFactory
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.autotests.data.RequestsData
import org.assertj.core.api.Assertions
import ru.viscur.autotests.restApiResources.QueRequests
import ru.viscur.autotests.utils.Helpers

@EnableAutoConfiguration
class QueueSmoke {

    companion object {
        private val log = LoggerFactory.getLogger(QueueSmoke::class.java)
    }

    val patientRef = Helpers.makeRefJson(RequestsData.red1, "Patient")

    @Test
    @Order(2)
    fun patientShouldBeDeletedFromQue() {
        QueRequests.deleteQue()
        QueRequests.addPatientToQue(patientRef)
        QueRequests.deletePatientFromQue(patientRef)
        Assertions.assertThat(QueRequests.getQueInfo()).doesNotContain(RequestsData.red1)
    }

    @Test
    @Order(1)
    fun patientShouldBeAddedToQue() {
        QueRequests.deleteQue()
        QueRequests.addPatientToQue(patientRef)
        Assertions.assertThat(QueRequests.getQueInfo()).contains(RequestsData.red1)
    }

}

