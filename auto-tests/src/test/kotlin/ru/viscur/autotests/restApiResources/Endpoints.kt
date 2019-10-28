package ru.viscur.autotests.restApiResources

class Endpoints {

    companion object {
        val BASE_URI: String = "http://84.201.154.121:8080"
        val CREATE_PATIENT: String = "$BASE_URI/reception/patient"
        val QUE_INFO: String = "$BASE_URI/queue/info"
        val QUE_ADD_PATIENT: String = "$BASE_URI/queue/patient/addToQueue"
        val QUE_DELETE_PATIENT : String = "$BASE_URI/queue/patient"
        val QUE_OFFICE_RDY : String = "$BASE_URI/queue/office/ready"
        val QUE_OFFICE_BUSY : String = "$BASE_URI/queue/office/busy"
        val QUE_DELETE_ALL: String = "$BASE_URI/queue"
    }

}