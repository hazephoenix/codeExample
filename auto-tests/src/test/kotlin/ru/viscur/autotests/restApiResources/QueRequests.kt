package ru.viscur.autotests.restApiResources

import io.restassured.RestAssured
import ru.viscur.autotests.utils.Helpers

class QueRequests {

    companion object {

        fun getQueInfo() : String  = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().get(Endpoints.QUE_INFO).
                then().statusCode(200).extract().body().asString()

        fun deleteQue() = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().delete(Endpoints.QUE_DELETE_ALL).then().statusCode(200)

        fun getCabinetRdy(cabinetRef: String) = Helpers.createRequestSpec(cabinetRef).
                `when`().
                post(Endpoints.QUE_OFFICE_RDY).
                then().statusCode(200)

        fun getCabinetBusy(cabinetRef: String) = Helpers.createRequestSpec(cabinetRef).
                `when`().
                post(Endpoints.QUE_OFFICE_BUSY).
                then().statusCode(200)

        fun addPatienToQue(patientRef: String) = Helpers.createRequestSpec(patientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)

        fun deletePatientFromQue(patientRef: String) = Helpers.createRequestSpec(patientRef).
                `when`().
                delete(Endpoints.QUE_DELETE_PATIENT).
                then().statusCode(200)

    }
}