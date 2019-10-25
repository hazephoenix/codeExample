package ru.viscur.autotests.utils

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import ru.viscur.autotests.data.RequestsData

class RequestSpec {
    companion object {
        fun createRequestSpec(body: String): RequestSpecification {
            return RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").body(body)
        }
    }
}