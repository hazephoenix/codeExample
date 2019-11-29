package ru.viscur.autotests.restApi

import io.restassured.RestAssured
import ru.viscur.autotests.dto.*
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.dto.PatientToExamine
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Reference

class QueRequests {

    companion object {

        //queue
        fun setQueueResortingConfig(value: Boolean) =
                Helpers.createRequestWithQuery(mapOf("value" to value)).`when`().
                        post(Endpoints.RECALC_QUEUE_CONFIG).
                        then().statusCode(200)

        fun setRecalcDurationConfig(severity: String, value: Boolean) =
                Helpers.createRequestWithQuery(mapOf("severity" to severity,"value" to value )).`when`().
                        post(Endpoints.RECALC_CLINICAL_IMPESSION_DURATION).
                        then().statusCode(200)

        fun getRecalcDurationConfig() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        post(Endpoints.RECALC_CLINICAL_IMPESSION_DURATION).
                        then().statusCode(200)

        fun getQueueResortingConfig() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.RECALC_QUEUE_CONFIG).
                        then().statusCode(200)

        fun deleteQue() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        delete(Endpoints.QUE_DELETE_ALL).
                        then().statusCode(200)

        fun cancelAllActivePatient() = getPatientsOfResponsable().patients.
                forEach() { patientInfo ->
                    cancelExamination(patientInfo.patientId)
                }

        fun getOfficeQue(officeRef: Reference) =
                Helpers.createRequestSpec(officeRef).`when`().
                        get(Endpoints.OFFICE_QUE).
                        then().statusCode(200).
                        extract().response().`as`(Bundle::class.java)

        fun queueItems() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.QUE_ITEMS).
                        then().statusCode(200).
                        extract().response().`as`(QueueItemsResponse::class.java)

        //patient
        fun addPatientToQue(patientRef: Reference) =
                Helpers.createRequestSpec(patientRef).`when`().
                        post(Endpoints.QUE_ADD_PATIENT).
                        then().statusCode(200)

        fun deletePatientFromQueue(patientRef: Reference) =
                Helpers.createRequestSpec(patientRef).`when`().
                        delete(Endpoints.QUE_DELETE_PATIENT).
                        then().statusCode(200)

        fun setPatientFirst(patientAndOfficeRef: ListResource) =
                Helpers.createRequestSpec(patientAndOfficeRef).`when`().
                        post(Endpoints.SET_PATIENT_FIRST).
                        then().statusCode(200)

        fun delayPatient(patientRef: Reference) =
                Helpers.createRequestSpec(patientRef).`when`().
                        post(Endpoints.DELAY_PATIENT).
                        then().statusCode(200)

        fun changeSeverity(patientId: String, severity: String) =
                Helpers.createRequestWithQuery(mapOf("patientId" to patientId, "severity" to severity)).`when`().
                        post(Endpoints.CHANGE_SEVERITY).
                        then().statusCode(200)

        //office
        fun officeIsReady(officeRef: Reference) =
                Helpers.createRequestSpec(officeRef).`when`().
                        post(Endpoints.QUE_OFFICE_READY).
                        then().statusCode(200)

        fun officeIsBusy(officeRef: Reference) =
                Helpers.createRequestSpec(officeRef).`when`().
                        post(Endpoints.QUE_OFFICE_BUSY).
                        then().statusCode(200)

        fun officeIsClosed(officeRef: Reference) =
                Helpers.createRequestSpec(officeRef).`when`().
                        post(Endpoints.QUE_OFFICE_CLOSE).
                        then().statusCode(200)

        fun invitePatientToOffice(patientAndOfficeRef: ListResource) =
                Helpers.createRequestSpec(patientAndOfficeRef).`when`().
                        post(Endpoints.OFFICE_INVITE).
                        then().statusCode(200)

        fun inviteNextPatientToOffice(officeRef: Reference) =
                Helpers.createRequestSpec(officeRef).`when`().
                        post(Endpoints.INVITE_SECOND_OFFICE).
                        then().log().all().statusCode(200)

        fun patientEntered(patientAndOfficeRef: ListResource) =
                Helpers.createRequestSpec(patientAndOfficeRef).log().all().`when`().
                        post(Endpoints.PATIENT_ENTERED).
                        then().statusCode(200).
                        extract().response().`as`(Bundle::class.java)
                        .let { it.entry.map { it.resource as ServiceRequest } }

        fun patientLeft(patientAndOfficeRef: ListResource) =
                Helpers.createRequestSpec(patientAndOfficeRef).`when`().
                        post(Endpoints.PATIENT_LEFT).
                        then().statusCode(200)

        fun cancelEntering(patientRef: Reference) =
                Helpers.createRequestSpec(patientRef).log().all().`when`().
                        post(Endpoints.CANCEL_ENTERING).
                        then().statusCode(200).extract().response()

        //patient
        fun createPatient(bundle : Bundle) =
                Helpers.createRequestSpec(bundle).`when`().
                        post(Endpoints.CREATE_PATIENT).
                        then().log().all().statusCode(200).
                        extract().response().`as`(Bundle::class.java)

        fun createBandagePatient(bundle : Bundle) =
                Helpers.createRequestSpec(bundle).`when`().
                        post(Endpoints.CREATE_BANDAGE_PATIENT).
                        then().statusCode(200).
                        extract().response().`as`(Bundle::class.java)

        fun <T> resource(resourceType: ResourceType<T>, id: String): T where T : BaseResource =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.BASE_URI + "/" + resourceType.id.toString() + "/" + id).
                        then().statusCode(200).
                        extract().response().`as`(resourceType.entityClass)

        //observation
        fun startObservation(serviceRequestId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.START_OBSERVATION + "?serviceRequestId=$serviceRequestId").
                        then().statusCode(200)
                        .extract()

        fun createObservation(observation : Observation) =
                Helpers.createRequestSpec(observation).log().all().`when`().
                        post(Endpoints.CREATE_OBSERVATION).
                        then().statusCode(200).log().all().
                        extract().response().`as`(Observation::class.java)

        fun updateObservation(observation : Observation) =
                Helpers.createRequestSpec(observation).log().all().`when`().
                        put(Endpoints.CREATE_OBSERVATION).
                        then().statusCode(200).log().all().
                        extract().response().`as`(Observation::class.java)

        fun observations(patientId: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.OBSERVATIONS + "?patientId=$patientId").
                        then().statusCode(200)
                        .extract().response().`as`(ObservationsResponse::class.java)

        fun addServiceRequests(bundle: Bundle) =
                Helpers.createRequestSpec(bundle).log().all().`when`().
                        post(Endpoints.ADD_SERVICE_REQUEST).
                        then().statusCode(200).
                        extract().response().`as`(CarePlan::class.java)

        //service requests, severity, diagnosis
        fun getSupposedServRequests(diagnosis : Any) =
                Helpers.createRequestSpec(diagnosis).log().all().`when`().
                        post(Endpoints.SUPPOSED_SERVICE_REQUEST).
                        then().statusCode(200).log().all().
                        extract().response().`as`(Bundle::class.java).
                        let { it.entry.filter {it.resource.resourceType == ResourceType.ResourceTypeId.ServiceRequest } }.map { it.resource as ServiceRequest }

        fun serviceRequestsOfPatients(patientId: String) =
                Helpers.createRequestSpecWithoutBody().
                        `when`().
                        get(Endpoints.SERVICE_REQUEST + "?patientId=$patientId").
                        then().statusCode(200).
                        extract().response().`as`(Bundle::class.java).
                        let { it.entry.map { it.resource as ServiceRequest } }

        fun cancelServiceRequest(serviceRequestId: String) =
                Helpers.createRequestSpecWithoutBody().`when`().log().all().
                        post(Endpoints.CANCEL_SERVICE_REQUEST + "?id=$serviceRequestId").
                        then().log().all().statusCode(200)

        fun cancelOfficeServiceRequests(patientId: String, officeId: String) =
                Helpers.createRequestWithQuery(mapOf("patientId" to patientId, "officeId" to officeId)).
                        `when`().log().all().post(Endpoints.CANCEL_OFFICE_SERVICE_REQUEST).
                        then().statusCode(200)

        fun getDiagnosis(bundle: Bundle, diagnosisCount: String) =
                Helpers.createRequestWithQueryAndBody(bundle, mapOf("take" to diagnosisCount)).
                        `when`().log().all().post(Endpoints.GET_DIAGNOSIS).
                        then().log().all().statusCode(200).extract().response().`as`(DiagnosesListInfo::class.java)

        fun getSeverity(bundle: Bundle, mainSyndromeCount: String? = null) =
                Helpers.createRequestSpec(bundle).
                        `when`().log().all().post(Endpoints.GET_SEVERITY + if (mainSyndromeCount == null) "" else "?takeSyndromes=$mainSyndromeCount").
                        then().log().all().statusCode(200)

        //examination
        fun completeExamination(bundle : Bundle) =
                Helpers.createRequestSpec(bundle).log().all().`when`().
                        post(Endpoints.COMPLETE_EXAMINATION).
                        then().statusCode(200).
                        extract().response().`as`(ClinicalImpression::class.java)

        fun cancelExamination(patientId : String) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.CANCEL_EXAMINATION + "?patientId=$patientId").
                        then().statusCode(200)

        fun patientsOfResp(practitionerId: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.PATIENTS_OF_RESP + if (practitionerId == null) "" else "?practitionerId=$practitionerId").
                        then().statusCode(200).
                        extract().response().`as`(PatientsOfRespResponse::class.java)["patients"]!!

        fun getPatientsOfResponsable(practitionerId: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.PATIENTS_OF_RESP + if (practitionerId == null) "" else "?practitionerId=$practitionerId").
                        then().statusCode(200).
                        extract().response().`as`(ActivePatientsInfo::class.java)

        //duration and reports
        fun getDefaultDuration() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_DEFAULT_DURATION).
                        then().statusCode(200).extract().`as`(Array<DefaultDurationInfo>::class.java)

        fun setDefaultDuration(severity: String, duration: Int) =
                Helpers.createRequestWithQuery(mapOf("severity" to severity, "duration" to duration)).`when`().
                        post(Endpoints.SET_DEFAULT_DURATION).
                        then().log().all().statusCode(200)

        fun getQueueReport() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.REPORT_QUEUE).
                        then().statusCode(200).
                        extract().response().`as`(Array<QueueReportInfo>::class.java)

        fun getOfficeQueueReport(officeId: String) =
                Helpers.createRequestWithQuery(mapOf("officeId" to officeId)).`when`().
                        get(Endpoints.REPORT_QUEUE).
                        then().statusCode(200).
                        extract().response().`as`(QueueReportInfo::class.java)

        fun getAllPractitionersWorkload() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.PRACTITIONER_WORKLOAD).
                        then().statusCode(200).
                        extract().response().`as`(Array<PractitionerWorkloadInfo>::class.java)

        fun getPractitionersWorkloadById(practitionerId: String) =
                Helpers.createRequestWithQuery(mapOf("practitionerId" to practitionerId)).`when`().
                        get(Endpoints.REPORT_QUEUE).
                        then().statusCode(200).
                        extract().response().`as`(PractitionerWorkloadInfo::class.java)

        fun getPatientsClinicalImpressionDuration() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.PATIENTS_CLINICAL_IMPESSION_DURATION).
                        then().statusCode(200).extract().`as`(Array<ClinicalImpressionDurationInfo>::class.java)

        fun getPatientObservationHistory(patientId: String) =
                Helpers.createRequestWithQuery(mapOf("patientId" to patientId)).`when`().
                        get(Endpoints.GET_OBSERVATION_HISTORY_OF_PATIENT).
                        then().log().all().statusCode(200).
                        extract().response().`as`(Array<ObservationHistoryInfo>::class.java)

        fun getPatientQueueHistory(patientId: String) =
                Helpers.createRequestWithQuery(mapOf("patientId" to patientId)).`when`().
                        get(Endpoints.GET_QUEUE_HISTORY_OF_PATIENT).
                        then().log().all().statusCode(200).
                        extract().response().`as`(Array<PatientQueueHistoryInfo>::class.java)

        //dictionaries
        fun getPractitioners(withBlocked: Boolean? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_PRACTITIONERS + if (withBlocked == null) "" else "?withBlocked=$withBlocked").
                        then().statusCode(200).
                        extract().response().`as`(Array<Practitioner>::class.java)

        fun createPractitioner(practitioner: Practitioner) =
                Helpers.createRequestSpec(practitioner).`when`().
                        post(Endpoints.GET_PRACTITIONERS).
                        then().statusCode(200)

        fun blockPractitioner(practitionerId: String, value: Boolean) =
                Helpers.createRequestWithQuery(mapOf("practitionerId" to practitionerId, "value" to value)).`when`().
                        post(Endpoints.BLOCK_PRACTITIONER).
                        then().statusCode(200)

        fun getPractitionerById(practitionerId: String) =
                Helpers.createRequestWithQuery(mapOf("id" to practitionerId)).`when`().
                        get(Endpoints.GET_PRACTITIONER_BY_ID).
                        then().statusCode(200).
                        extract().response().`as`(Practitioner::class.java)

        fun setPractitionerActivityAndLocation(practitionerId: String, onWorkValue: Boolean, officeId: String? = null) =
                Helpers.createRequestWithQuery(mapOf("practitionerId" to practitionerId, "value" to onWorkValue) + if (officeId == null) mapOf() else mapOf("officeId" to officeId)).`when`().
                        post(Endpoints.SET_PRACTITIONER_ACTIVITY).
                        then().statusCode(200)

        fun getIcdToObservationTypes() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_ICD_TO_OBSERVATION_TYPES).
                        then().statusCode(200).
                        extract().response().`as`(Array<CodeMap>::class.java)

        fun getObservationTypes(parentCode: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_OBSERVATION_TYPES + if (parentCode == null) "" else "?parentCode=$parentCode").
                        then().statusCode(200).
                        extract().response().`as`(Array<Concept>::class.java)

        fun getIcdToPractitionerQualification() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_ICD_TO_PRACTITIONER_QUALIFICATION).
                        then().statusCode(200).
                        extract().response().`as`(Array<CodeMap>::class.java)

        fun getRespQualificationToObservationTypes() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_RESP_QUALIFICATION_TO_OBSERVATION_TYPES).
                        then().statusCode(200).
                        extract().response().`as`(Array<CodeMap>::class.java)

        fun getCodeInfo(codeName: String, parentCode: String? = null) =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_CODE_INFO + "/$codeName" + if (parentCode == null) "" else "?parentCode=$parentCode").
                        then().statusCode(200).
                        extract().response().`as`(Array<Concept>::class.java)

        fun getOffices() =
                Helpers.createRequestSpecWithoutBody().`when`().
                        get(Endpoints.GET_OFFICES).
                        then().statusCode(200).
                        extract().response().`as`(Array<Location>::class.java)

    }
}

class QueueItemsResponse: ArrayList<QueueItem>()

class ObservationsResponse: ArrayList<Observation>()

class PatientsOfRespResponse: HashMap<String, List<PatientToExamine>>()