package ru.viscur.autotests.restApi

class Endpoints {

    companion object {
        const val BASE_URI: String = "http://localhost:8080"
        //const val BASE_URI: String = "http://84.201.154.121:8080"
        //Patient
        const val CREATE_PATIENT: String = "$BASE_URI/reception/patient"
        const val CREATE_BANDAGE_PATIENT: String = "$BASE_URI/reception/patientForBandage"
        //Queue
        const val PATIENTS_CLINICAL_IMPESSION_DURATION = "$BASE_URI/clinicalImpressionDuration"
        const val SET_DEFAULT_DURATION = "$BASE_URI/clinicalImpressionDuration/duration"
        const val RECALC_CLINICAL_IMPESSION_DURATION = "$BASE_URI/clinicalImpressionDuration/autoRecalc"
        const val RECALC_QUEUE_CONFIG = "$BASE_URI/queue/recalcNextOffice"
        const val QUE_ADD_PATIENT: String = "$BASE_URI/queue/patient/addToQueue"
        const val QUE_DELETE_PATIENT: String = "$BASE_URI/queue/patient"
        const val QUE_DELETE_ALL: String = "$BASE_URI/queue"
        const val QUE_ITEMS: String = "$BASE_URI/queue/queueItems"
        const val SET_PATIENT_FIRST = "$BASE_URI/queue/office/setAsFirst"
        const val DELAY_PATIENT = "$BASE_URI/queue/office/delayGoingToObservation"
        const val CHANGE_SEVERITY = "$BASE_URI/examination/severity"
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
        //diagnosis
        const val GET_DIAGNOSIS: String = "$BASE_URI/reception/diagnostic"
        //severity
        const val GET_SEVERITY: String = "$BASE_URI/reception/severity"
        //reports and duration
        const val GET_DEFAULT_DURATION: String = "$BASE_URI/clinicalImpressionDuration/default"
        const val REPORT_QUEUE: String = "$BASE_URI/report/queue"
        const val PRACTITIONER_WORKLOAD = "$BASE_URI/report/workload"
        const val GET_OBSERVATION_HISTORY_OF_PATIENT = "$BASE_URI/report/observationHistoryOfPatient"
        const val GET_QUEUE_HISTORY_OF_PATIENT = "$BASE_URI/report/queueHistoryOfPatient"
        //dictionaries
        const val GET_PRACTITIONERS = "$BASE_URI/practitioner"
        const val BLOCK_PRACTITIONER = "$BASE_URI/practitioner/blocked"
        const val GET_PRACTITIONER_BY_ID = "$BASE_URI/practitioner/byId"
        const val GET_ICD_TO_OBSERVATION_TYPES = "$BASE_URI/dictionary/icdToObservationTypes"
        const val GET_OBSERVATION_TYPES = "$BASE_URI/dictionary/Observation_types"
        const val GET_ICD_TO_PRACTITIONER_QUALIFICATION = "$BASE_URI/dictionary/icdToPractitionerQualifications"
        const val GET_RESP_QUALIFICATION_TO_OBSERVATION_TYPES = "$BASE_URI/dictionary/respQualificationToObservationTypes"
        const val GET_OFFICES = "$BASE_URI/dictionary/offices"
        const val GET_CODE_INFO = "$BASE_URI/dictionary"
    }
}