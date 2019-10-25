package ru.viscur.autotests.tests

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.autotests.data.RequestsData
import ru.viscur.autotests.restApiResources.Endpoints
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.RequestSpec

@EnableAutoConfiguration
class DobavlenieRedYellow {

    val redPatientRef = Helpers.makeRef(RequestsData.idRedPatient1, "Patient")
    val yellowPatientRef = Helpers.makeRef(RequestsData.idYellowPatient1, "Patient")
    val cabinet101Ref = Helpers.makeRef("Office:101", "Location")
    val expectedQueString = "<br/>  QueueItem(0, 1000, IN_QUEUE, RED, subject=Ref(reference='Patient/127a0f9c-c04a-4b00-be79-1fa44873f128', type=Patient), location=Ref(reference='Location/Office:101', type=Location))\n" +
            "<br/>  QueueItem(1, 1000, IN_QUEUE, YELLOW, subject=Ref(reference='Patient/4b195cf0-aec2-432e-8978-a3f94acd0b86', type=Patient), location=Ref(reference='Location/Office:101', type=Location))"

    @BeforeEach
    fun deleteQue() {
        Helpers.deleteQue()
    }

    @AfterEach
    fun delete() {
        Helpers.deleteQue()
    }

    @Test
    @Order(1)
    fun redPatientShouldBeFirstInQue() {

        //подготовка кабинета
        RequestSpec.createRequestSpec(cabinet101Ref).
                `when`().
                post(Endpoints.QUE_OFFICE_BUSY)

        //добавление yellow в очередь
        RequestSpec.createRequestSpec(yellowPatientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)

        //добавление red в очередь
        RequestSpec.createRequestSpec(redPatientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)
        Assertions.assertThat(Helpers.getQueInfo()).contains(expectedQueString)
    }

}
