package ru.viscur.autotests.restApi

class Endpoints {

    companion object {
        const val BASE_URI: String = "http://localhost:8080"
        //const val BASE_URI: String = "http://84.201.154.121:8080"
        //Patient
        const val CREATE_PATIENT: String = "$BASE_URI/reception/patient"
        //Queue
        const val CLINICAL_IMPESSION_DURATION = "$BASE_URI/clinicalImpressionDuration"
        const val RECALC_CLINICAL_IMPESSION_DURATION = "$BASE_URI/clinicalImpressionDuration/autoRecalc"
        const val RECALC_QUEUE_CONFIG = "$BASE_URI/queue/recalcNextOffice"
        const val QUE_INFO: String = "$BASE_URI/queue/info"
        const val QUE_ADD_PATIENT: String = "$BASE_URI/queue/patient/addToQueue"
        const val QUE_DELETE_PATIENT: String = "$BASE_URI/queue/patient"
        const val QUE_DELETE_ALL: String = "$BASE_URI/queue"
        const val QUE_ITEMS: String = "$BASE_URI/queue/queueItems"
        const val SET_PATIENT_FIRST = "$BASE_URI/queue/office/setAsFirst"
        const val DELAY_PATIENT = "$BASE_URI/queue/office/delayGoingToObservation"

        //office
        const val OFFICE_INVITE: String = "$BASE_URI/queue/office/forceSendPatientToObservation"
        const val OFFICE_QUE: String = "$BASE_URI/queue"
        const val QUE_OFFICE_READY: String = "$BASE_URI/queue/office/ready"
        const val QUE_OFFICE_BUSY: String = "$BASE_URI/queue/office/busy"
        const val QUE_OFFICE_CLOSE: String = "$BASE_URI/queue/office/closed"
        const val PATIENT_ENTERED: String = "$BASE_URI/queue/office/patientEntered"
        const val PATIENT_LEFT: String = "$BASE_URI/queue/office/patientLeft"
        const val CANCEL_ENTERING: String = "$BASE_URI/queue/office/cancelEntering"
        const val INVITE_SECOND_OFFICE: String = "$BASE_URI/queue/office/nextPatient"
        //observation
        const val START_OBSERVATION: String = "$BASE_URI/Observation/start"
        const val CREATE_OBSERVATION: String = "$BASE_URI/Observation"
        const val OBSERVATIONS: String = "$BASE_URI/Observation"
        const val SUPPOSED_SERVICE_REQUEST: String = "$BASE_URI/reception/serviceRequests"
        const val ADD_SERVICE_REQUEST = "$BASE_URI/examination/serviceRequests  "
        //examination
        const val COMPLETE_EXAMINATION: String = "$BASE_URI/examination"
        const val PATIENTS_OF_RESP: String = "$BASE_URI/examination/patients"
        const val SERVICE_REQUEST: String = "$BASE_URI/examination/serviceRequests"
        const val CANCEL_EXAMINATION: String = "$BASE_URI/examination/cancel"
        //service request
        const val PATIENT_SERVICE_REQUESTS: String = "$BASE_URI/examination/serviceRequests"
        const val CANCEL_SERVICE_REQUEST: String = "$BASE_URI/examination/serviceRequests/cancelById"
        const val CANCEL_OFFICE_SERVICE_REQUEST: String = "$BASE_URI/examination/serviceRequests/cancel"
        //reports and duration
        const val DEFAULT_DURATION: String = "$BASE_URI/clinicalImpressionDuration/default"

    }
}