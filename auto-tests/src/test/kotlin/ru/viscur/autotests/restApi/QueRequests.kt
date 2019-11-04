package ru.viscur.autotests.restApi

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Reference

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
                then().statusCode(200).extract().response().`as`(Bundle::class.java)

        fun addPatientToQue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)

        fun deletePatientFromQueue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                delete(Endpoints.QUE_DELETE_PATIENT).
                then()

        fun queueItems() = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().get(Endpoints.QUE_ITEMS).
                then().statusCode(200).extract().response().`as`(QueueItemsResponse::class.java)

        //cabinet
        fun officeIsReady(officeRef: Reference) = Helpers.createRequestSpec(officeRef).
                `when`().
                post(Endpoints.QUE_OFFICE_READY).
                then().statusCode(200)

        fun officeIsBusy(officeRef: Reference) = Helpers.createRequestSpec(officeRef).
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
                then().statusCode(200).extract().response().`as`(Bundle::class.java)
                .let { it.entry.map { it.resource as ServiceRequest } }

        //patient
        fun createPatient(bundle : Bundle) = Helpers.createRequestSpec(bundle).
                `when`().
                post(Endpoints.CREATE_PATIENT).
                then().statusCode(200).extract().response().`as`(Bundle::class.java)

        fun <T> resource(resourceType: ResourceType<T>, id: String): T
                where T : BaseResource =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.BASE_URI + "/" + resourceType.id.toString() + "/" + id).then().statusCode(200)
                        .extract().response().`as`(resourceType.entityClass)

        //observation
        fun createObservation(observation : Observation) = Helpers.createRequestSpec(observation).log().all().
                `when`().
                post(Endpoints.CREATE_OBSERVATION).
                then().statusCode(200)

        fun addServiceRequests(bundle: Bundle) = Helpers.createRequestSpec(bundle).log().all().
                `when`().
                post(Endpoints.ADD_SERVICE_REQUEST).
                then().statusCode(200).extract().response().`as`(CarePlan::class.java)

        //service requests, severity, diagnosis
        fun getSupposedServRequests(diagnosis : Any) = Helpers.createRequestSpec(diagnosis).log().all().
                `when`().
                post(Endpoints.SUPPOSED_SERVICE_REQUEST).
                then().statusCode(200)

        //examination
        fun completeExamination(bundle : Bundle) = Helpers.createRequestSpec(bundle).log().all().
                `when`().
                post(Endpoints.COMPLETE_EXAMINATION).
                then().statusCode(200).extract().response().`as`(ClinicalImpression::class.java)
    }
}

class QueueItemsResponse: ArrayList<QueueItem>()