package ru.viscur.autotests.restApiResources

import io.restassured.RestAssured
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.type.Reference
import java.io.File

class QueRequests {

    companion object {

        //queue
        fun getQueInfo() : String  = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().get(Endpoints.QUE_INFO).
                then().statusCode(200).extract().body().asString()

        fun deleteQue() = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().delete(Endpoints.QUE_DELETE_ALL).then().statusCode(200)

        fun getOfficeQue(cabinetRef: Reference) = Helpers.createRequestSpec(cabinetRef).
                `when`().get(Endpoints.OFFICE_QUE).
                then().statusCode(200)

        fun addPatientToQue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)

        fun deletePatientFromQue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                delete(Endpoints.QUE_DELETE_PATIENT).
                then()

        //cabinet
        fun getCabinetRdy(cabinetRef: Reference) = Helpers.createRequestSpec(cabinetRef).
                `when`().
                post(Endpoints.QUE_OFFICE_RDY).
                then().statusCode(200)

        fun getCabinetBusy(cabinetRef: Reference) = Helpers.createRequestSpec(cabinetRef).
                `when`().
                post(Endpoints.QUE_OFFICE_BUSY).
                then().statusCode(200)

        fun invitePatientToOffice(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).
                `when`().
                post(Endpoints.OFFICE_INVITE).
                then().statusCode(200)

        fun patientEntered(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).log().all().
                `when`().
                post(Endpoints.PATIENT_ENTERED).
                then().statusCode(200)

        //patient
        fun createPatient(json : Any) = Helpers.createRequestSpec(json).
                `when`().
                post(Endpoints.CREATE_PATIENT).
                then().statusCode(200)

        fun getResource(resourceType: String, id: String) = Helpers.createRequestSpecWithoutBody().
                `when`().
                get(Endpoints.BASE_URI + "/" + resourceType + "/" + id).
                then().statusCode(200)

        //observation
        fun createObservation(observation : Observation) = Helpers.createRequestSpec(observation).log().all().
        `when`().
        post(Endpoints.CREATE_OBSERVATION).
        then().statusCode(200)

        //service requests, severity, diagnosis
        fun getServiceRequests(diagnosis : Any) = Helpers.createRequestSpec(diagnosis).log().all().
                `when`().
                post(Endpoints.SERVICE_REQUEST).
                then().statusCode(200)

    }
}