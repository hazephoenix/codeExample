package ru.viscur.autotests.utils

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification

class Helpers {

    companion object {
        //создать ссылку
        fun makeRefJson(id: String, resourceType: String): String = "{\"reference\": \"$resourceType/$id\", \"resourceType\": \"$resourceType\"}"

        //создать спецификацию запроса
        fun createRequestSpec(body: String): RequestSpecification {
            return RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").body(body)
        }
    }
}