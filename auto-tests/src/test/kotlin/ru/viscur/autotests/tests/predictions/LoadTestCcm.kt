package ru.viscur.autotests.tests.predictions

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.http.Header
import org.junit.jupiter.api.Test

class LoadTestCcm {
    var CMM_API = "http://192.168.10.49:8080/api/currentSurvey/save"

    @Test
    fun test() {
        /*for (int i = 1; i < 9; i++) {
        }*/
        val bearerToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3NzciLCJyb2xlcyI6WyJBRE1JTiJdLCJpYXQiOjE1OTk2NDEwOTJ9.kB6Eoa9A7s0cK24wYoZTtBwXPMcBIZlEmkHRlQ7adEQ"
        val payload2 = """{
  "rfid": "1"
}"""
        val payload = """{
  "id": 999,
  "user": {
    "id": 2,
    "rfid": 2
  },
  "photo": "",
  "beginDate": null,
  "endDate": null,
  "duration": null,
  "result": null,
  "alcohol": 990000.121,
  "bodyTemperature": null,
  "systolic": null,
  "diastolic": null,
  "heartRate": null,
  "complaints": null,
  "signature": "",
  "createdAt": "2020-07-13T10:53:44.662+0000",
  "updatedAt": "2020-07-13T10:53:44.666+0000",
  "deletedAt": null
}"""
        val header = Header("Authorization", "Bearer $bearerToken")
        RestAssured.given().headers("Content-type", ContentType.JSON, header).body(payload).log().all().`when`().post(CMM_API).then().log().all().statusCode(200)
    }
}