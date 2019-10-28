package ru.viscur.autotests.tests

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.autotests.data.RequestsData
import ru.viscur.autotests.restApiResources.QueRequests
import ru.viscur.autotests.restApiResources.QueRequests.Companion.addPatienToQue
import ru.viscur.autotests.restApiResources.QueRequests.Companion.getCabinetBusy
import ru.viscur.autotests.utils.Helpers

@EnableAutoConfiguration
class Queue {

    val greenPatientRef = Helpers.makeRefJson(RequestsData.green1, "Patient")
    val redPatientRef = Helpers.makeRefJson(RequestsData.red1, "Patient")
    val yellowPatientRef = Helpers.makeRefJson(RequestsData.yellow1, "Patient")
    val cabinet101Ref = Helpers.makeRefJson("Office:101", "Location")
    val expectedQueString = "QueueItem(0, 1000, IN_QUEUE, RED, subject=Ref(reference='Patient/127a0f9c-c04a-4b00-be79-1fa44873f128', type=Patient), location=Ref(reference='Location/Office:101', type=Location))\n" +
            "<br/>  QueueItem(1, 1000, IN_QUEUE, YELLOW, subject=Ref(reference='Patient/4b195cf0-aec2-432e-8978-a3f94acd0b86', type=Patient), location=Ref(reference='Location/Office:101', type=Location))\n" +
            "<br/>  QueueItem(2, 1000, IN_QUEUE, GREEN, subject=Ref(reference='Patient/1b6f6be6-1ac6-4bb4-9ceb-8db549cc77e4', type=Patient), location=Ref(reference='Location/Office:101', type=Location))"

    @Test
    @Order(1)
    fun redPatientShouldBeFirstInQue() {
        //подготовка кабинета
        QueRequests.deleteQue()
        getCabinetBusy(cabinet101Ref)

        //добавление green, yellow, red в очередь
        addPatienToQue(greenPatientRef)
        addPatienToQue(yellowPatientRef)
        addPatienToQue(redPatientRef)

        //проверка, что red первый, yellow второй и green третий
        assertThat(QueRequests.getQueInfo()).contains(expectedQueString)
    }

}
