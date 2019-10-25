package ru.viscur.autotests.tests

import io.restassured.RestAssured
import org.slf4j.LoggerFactory
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.autotests.data.RequestsData
import ru.viscur.autotests.restApiResources.Endpoints
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.RequestSpec

@EnableAutoConfiguration
class DobavlenieUdalenieIzOcheredi {

    val addPatientToQueSpec = RequestSpec.createRequestSpec(Helpers.makeRef(RequestsData.idRedPatient1, "Patient"))
    val setCabinetRdySpec = RequestSpec.createRequestSpec(Helpers.makeRef("Office:101", "Location"))
    val office101rdy: String = "queue for Office:101 (READY)"

    companion object {
        private val log = LoggerFactory.getLogger(DobavlenieUdalenieIzOcheredi::class.java)
    }

    @Test
    @Order(1)
    fun cabinetShouldBeRdy() {
        Helpers.deleteQue()
        RestAssured.given(setCabinetRdySpec).`when`().
                post(Endpoints.QUE_OFFICE_RDY)

        Assertions.assertThat(Helpers.getQueInfo()).contains(office101rdy)
    }

    @Test
    @Order(2)
    fun patientDeletingFromQue() {
        RestAssured.given(setCabinetRdySpec).`when`().
                post(Endpoints.QUE_OFFICE_RDY)

        Assertions.assertThat(Helpers.getQueInfo()).contains(office101rdy)

        RestAssured.given(addPatientToQueSpec).`when`().
                delete(Endpoints.QUE_DELETE_PATIENT)

        Assertions.assertThat(Helpers.getQueInfo()).doesNotContain(RequestsData.idRedPatient1)
    }

    @Test
    @Order(3)
    fun patientShouldBeInQueAfterAdding() {
        Helpers.deleteQue()
        /*val sr = ServiceRequest("1", code = CodeableConcept("Health_Rate", "ValueSet/Observation_types"))
        Bundle(entry = listOf(BundleEntry(ServiceRequest(code = CodeableConcept(systemId = "asdf", code = "asdf")))))*/
        RestAssured.given(addPatientToQueSpec).
                `when`().post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)
        Assertions.assertThat(Helpers.getQueInfo()).contains(RequestsData.idRedPatient1)
    }

}

