package ru.viscur.autotests.tests.predictions

import Json4Kotlin_Base
import User
import io.restassured.RestAssured
import io.restassured.http.Header
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import java.util.Arrays.asList


class DiagnosisPrediction {

    @Test
    fun predictDiagnosis() {

        load()
    }
    @Test
    fun predictDiagnosis2() {

        load()
    }
    @Test
    fun predictDiagnosis3() {

        load()
    }
    @Test
    fun predictDiagnosis4() {

        load()
    }
    @Test
    fun predictDiagnosis7() {

        load()
    }
    @Test
    fun predictDiagnosis8() {

        load()
    }
    @Test
    fun predictDiagnosis9() {

        load()
    }
    @Test
    fun predictDiagnosis10() {

        load()
    }

     fun load() {
        val CMM_API: String = "http://192.168.10.46:8080/api/currentSurvey/save"
        val bearerToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3NzciLCJyb2xlcyI6WyJBRE1JTiJdLCJpYXQiOjE2MDAxNTg2MjN9.DLp9ZlNXOBr5KwV0_8guzBTiUoEcy2X9ert81JXQ-H0"
        val filePath: String = "./src/test/kotlin/ru/viscur/autotests/tests/predictions/JPG.jpg"
        val fileContent: ByteArray = FileUtils.readFileToByteArray(File(filePath))
        val encodedString: String = Base64.getEncoder().encodeToString(fileContent)
        val payload3 = Json4Kotlin_Base(id = 999999, user = User(2, 2), photo = encodedString, alcohol = 990000.121,
                createdAt = "2020-07-13T10:53:44.662+0000",
                updatedAt = "2020-07-13T10:53:44.662+0000",
                beginDate = null,
                endDate = null,
                duration = null,
                result = null,
                bodyTemperature = null,
                systolic = null,
                diastolic = null,
                heartRate = null,
                complaints = null,
                signature = null,
                deletedAt = null
        )
        val header = Header("Authorization", "Bearer $bearerToken")
        val header2 = Header("Content-type", "application/json")
        for (i in 0..5000) {
            RestAssured.given().header("Authorization", "Bearer $bearerToken").header("Content-type", "application/json").body(payload3).`when`().put(CMM_API)
            Thread.sleep(800)
        }
    }

}