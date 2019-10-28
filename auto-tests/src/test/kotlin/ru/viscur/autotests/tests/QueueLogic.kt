package ru.viscur.autotests.tests

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.autotests.data.RequestsData
import ru.viscur.autotests.restApiResources.QueRequests
import ru.viscur.autotests.restApiResources.QueRequests.Companion.addPatientToQue
import ru.viscur.autotests.restApiResources.QueRequests.Companion.getCabinetBusy
import ru.viscur.autotests.restApiResources.QueRequests.Companion.getCabinetRdy
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Reference

@EnableAutoConfiguration
class QueueLogic {

    val greenPatientRef = Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = RequestsData.green1)
    val redPatientRef = Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = RequestsData.red1)
    val yellowPatientRef = Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = RequestsData.yellow1)
    val cabinet101Ref = Reference(resourceType = ResourceType.ResourceTypeId.Location, id = "Office:101")

    @Test
    @Order(1)
    fun redPatientShouldBeFirstInQue() {
        val expectedQueOrder = "QueueItem(0, 1000, IN_QUEUE, RED, subject=Ref(reference='Patient/127a0f9c-c04a-4b00-be79-1fa44873f128', type=Patient), location=Ref(reference='Location/Office:101', type=Location))\n" +
                "<br/>  QueueItem(1, 1000, IN_QUEUE, YELLOW, subject=Ref(reference='Patient/4b195cf0-aec2-432e-8978-a3f94acd0b86', type=Patient), location=Ref(reference='Location/Office:101', type=Location))\n" +
                "<br/>  QueueItem(2, 1000, IN_QUEUE, GREEN, subject=Ref(reference='Patient/1b6f6be6-1ac6-4bb4-9ceb-8db549cc77e4', type=Patient), location=Ref(reference='Location/Office:101', type=Location))"
        //подготовка кабинета
        QueRequests.deleteQue()
        getCabinetBusy(cabinet101Ref)

        //добавление green, yellow, red в очередь
        addPatientToQue(greenPatientRef)
        addPatientToQue(yellowPatientRef)
        addPatientToQue(redPatientRef)

        //проверка, что red первый, yellow второй и green третий
        assertThat(QueRequests.getQueInfo()).contains(expectedQueOrder)
    }

    @Test
    @Order(2)
    fun redShouldBeSecondWhenGreenGoingToObservation() {
        val expectedQueOrder = "QueueItem(0, 1000, GOING_TO_OBSERVATION, GREEN, subject=Ref(reference='Patient/1b6f6be6-1ac6-4bb4-9ceb-8db549cc77e4', type=Patient), location=Ref(reference='Location/Office:101', type=Location))\n" +
                "<br/>  QueueItem(1, 1000, IN_QUEUE, RED, subject=Ref(reference='Patient/127a0f9c-c04a-4b00-be79-1fa44873f128', type=Patient), location=Ref(reference='Location/Office:101', type=Location))"
        //подготовка кабинета
        QueRequests.deleteQue()
        getCabinetRdy(cabinet101Ref)

        //добавление зеленого и красного
        addPatientToQue(greenPatientRef)
        addPatientToQue(redPatientRef)

        //проверка, что зеленый первый в очереди
        assertThat(QueRequests.getQueInfo()).contains(expectedQueOrder)
    }

}
