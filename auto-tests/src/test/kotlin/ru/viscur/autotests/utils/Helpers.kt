package ru.viscur.autotests.utils

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification

class Helpers {

    companion object {
        //создать спецификацию запроса RestApi
        fun createRequestSpec(body: Any): RequestSpecification {
            return RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").body(body)
        }
    }
}