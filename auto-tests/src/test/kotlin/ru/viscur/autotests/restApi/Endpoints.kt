package ru.viscur.autotests.restApi

class Endpoints {

    companion object {
        const val BASE_URI: String = "http://localhost:8080"
        //        const val BASE_URI: String = "http://84.201.154.121:8080"
        //Patient
        const val CREATE_PATIENT: String = "$BASE_URI/reception/patient"
        const val PATIENT_ENTERED: String = "$BASE_URI/queue/office/patientEntered"
        //Queue
        const val QUE_INFO: String = "$BASE_URI/queue/info"
        const val QUE_ADD_PATIENT: String = "$BASE_URI/queue/patient/addToQueue"
        const val QUE_DELETE_PATIENT: String = "$BASE_URI/queue/patient"
        const val QUE_OFFICE_READY: String = "$BASE_URI/queue/office/ready"
        const val QUE_OFFICE_BUSY: String = "$BASE_URI/queue/office/busy"
        const val QUE_DELETE_ALL: String = "$BASE_URI/queue"
        //office
        const val OFFICE_INVITE: String = "$BASE_URI/queue/office/forceSendPatientToObservation"
        const val OFFICE_QUE: String = "$BASE_URI/queue"
        //observation
        const val CREATE_OBSERVATION: String = "$BASE_URI/Observation"
        const val SERVICE_REQUEST: String = "$BASE_URI/reception/serviceRequests"
        //examination
        const val COMPLETE_EXAMINATION: String = "$BASE_URI/examination"


    }

}