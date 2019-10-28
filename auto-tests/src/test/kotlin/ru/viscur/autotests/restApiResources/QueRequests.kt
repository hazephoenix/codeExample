package ru.viscur.autotests.restApiResources

import io.restassured.RestAssured
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.type.Reference

class QueRequests {

    companion object {

        fun getQueInfo() : String  = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().get(Endpoints.QUE_INFO).
                then().statusCode(200).extract().body().asString()

        fun deleteQue() = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().delete(Endpoints.QUE_DELETE_ALL).then().statusCode(200)

        fun getCabinetRdy(cabinetRef: Reference) = Helpers.createRequestSpec(cabinetRef).
                `when`().
                post(Endpoints.QUE_OFFICE_RDY).
                then().statusCode(200)

        fun getCabinetBusy(cabinetRef: Reference) = Helpers.createRequestSpec(cabinetRef).
                `when`().
                post(Endpoints.QUE_OFFICE_BUSY).
                then().statusCode(200)

        fun addPatientToQue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)

        fun deletePatientFromQue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                delete(Endpoints.QUE_DELETE_PATIENT).
                then().statusCode(200)

        fun invitePatientToOffice(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).
                `when`().
                post(Endpoints.OFFICE_INVITE).
                then().statusCode(200)

    }
}