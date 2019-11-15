package ru.viscur.autotests.restApi

import io.restassured.RestAssured
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.dto.PatientToExamine
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

        fun getOfficeQue(officeRef: Reference) = Helpers.createRequestSpec(officeRef).
                `when`().get(Endpoints.OFFICE_QUE).
                then().statusCode(200).extract().response().`as`(Bundle::class.java)

        fun queueItems() = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().get(Endpoints.QUE_ITEMS).
                then().statusCode(200).extract().response().`as`(QueueItemsResponse::class.java)

        //patient
        fun addPatientToQue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                post(Endpoints.QUE_ADD_PATIENT).
                then().statusCode(200)

        fun deletePatientFromQueue(patientRef: Reference) = Helpers.createRequestSpec(patientRef).
                `when`().
                delete(Endpoints.QUE_DELETE_PATIENT).
                then()
        fun setPatientFirst(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).
                `when`().
                post(Endpoints.SET_PATIENT_FIRST).
                then().statusCode(200)
        //office
        fun officeIsReady(officeRef: Reference) = Helpers.createRequestSpec(officeRef).
                `when`().
                post(Endpoints.QUE_OFFICE_READY).
                then().statusCode(200)

        fun officeIsBusy(officeRef: Reference) = Helpers.createRequestSpec(officeRef).
                `when`().
                post(Endpoints.QUE_OFFICE_BUSY).
                then().statusCode(200)

        fun officeIsClosed(officeRef: Reference) = Helpers.createRequestSpec(officeRef).
                `when`().
                post(Endpoints.QUE_OFFICE_CLOSE).
                then().statusCode(200)

        fun invitePatientToOffice(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).
                `when`().
                post(Endpoints.OFFICE_INVITE).
                then().statusCode(200)

        fun inviteNextPatientToOffice(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).
                `when`().
                post(Endpoints.INVITE_SECOND_OFFICE).
                then().statusCode(200)

        fun patientEntered(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).log().all().
                `when`().
                post(Endpoints.PATIENT_ENTERED).
                then().statusCode(200).extract().response().`as`(Bundle::class.java)
                .let { it.entry.map { it.resource as ServiceRequest } }

        fun patientLeft(patientAndOfficeRef: ListResource) = Helpers.createRequestSpec(patientAndOfficeRef).
                `when`().
                post(Endpoints.PATIENT_LEFT).
                then().statusCode(200)

        fun cancelEntering(officeRef: Reference) = Helpers.createRequestSpec(officeRef).log().all().
                `when`().
                post(Endpoints.CANCEL_ENTERING).
                then().statusCode(200).extract().response()

        //patient
        fun createPatient(bundle : Bundle) = Helpers.createRequestSpec(bundle).
                `when`().
                post(Endpoints.CREATE_PATIENT).
                then().log().all().statusCode(200).extract().response().`as`(Bundle::class.java)

        fun <T> resource(resourceType: ResourceType<T>, id: String): T
                where T : BaseResource =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.BASE_URI + "/" + resourceType.id.toString() + "/" + id).then().statusCode(200)
                        .extract().response().`as`(resourceType.entityClass)

        //observation
        fun startObservation(serviceRequestId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.START_OBSERVATION + "?serviceRequestId=$serviceRequestId").then().statusCode(200)
                        .extract()

        fun createObservation(observation : Observation) = Helpers.createRequestSpec(observation).log().all().
                `when`().
                post(Endpoints.CREATE_OBSERVATION).
                then().statusCode(200).log().all().extract().response().`as`(Observation::class.java)

        fun updateObservation(observation : Observation) = Helpers.createRequestSpec(observation).log().all().
                `when`().
                put(Endpoints.CREATE_OBSERVATION).
                then().statusCode(200).log().all().extract().response().`as`(Observation::class.java)

        fun observations(patientId: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.OBSERVATIONS + "?patientId=$patientId").then().statusCode(200)
                        .extract().response().`as`(ObservationsResponse::class.java)

        fun addServiceRequests(bundle: Bundle) = Helpers.createRequestSpec(bundle).log().all().
                `when`().
                post(Endpoints.ADD_SERVICE_REQUEST).
                then().statusCode(200).extract().response().`as`(CarePlan::class.java)

        //service requests, severity, diagnosis
        fun getSupposedServRequests(diagnosis : Any) = Helpers.createRequestSpec(diagnosis).log().all().
                `when`().
                post(Endpoints.SUPPOSED_SERVICE_REQUEST).
                then().statusCode(200)

        fun serviceRequestsOfPatients(patientId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.SERVICE_REQUEST + "?patientId=$patientId").then().statusCode(200)
                        .extract().response().`as`(Bundle::class.java)
                        .let { it.entry.map { it.resource as ServiceRequest } }

        fun cancelServiceRequest(serviceRequestId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().log().all().
                        post(Endpoints.CANCEL_SERVICER_REQUEST + "?id=$serviceRequestId").then().log().all().
                        statusCode(200)

        fun cancelOfficeServiceRequests(patientId: String, officeId: String) =
                Helpers.createRequestWithQuery(mapOf("patientId" to patientId, "officeId" to officeId)).`when`().log().all().
                        post(Endpoints.CANCEL_SERVICER_REQUEST).then().
                        statusCode(200)

        //examination
        fun completeExamination(bundle : Bundle) = Helpers.createRequestSpec(bundle).log().all().
                `when`().
                post(Endpoints.COMPLETE_EXAMINATION).
                then().statusCode(200).extract().response().`as`(ClinicalImpression::class.java)

        fun cancelExamination(patientId : String) = Helpers.createRequestSpecWithoutBody().
                `when`().
                get(Endpoints.CANCEL_EXAMINATION + "?patientId=$patientId").
                then().statusCode(200)

        fun patientsOfResp(practitionerId: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().get(Endpoints.PATIENTS_OF_RESP + if (practitionerId == null) "" else "?practitionerId=$practitionerId").then().statusCode(200)
                        .extract().response().`as`(PatientsOfRespResponse::class.java)["patients"]!!
    }
}

class QueueItemsResponse: ArrayList<QueueItem>()

class ObservationsResponse: ArrayList<Observation>()

class PatientsOfRespResponse: HashMap<String, List<PatientToExamine>>()