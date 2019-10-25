package ru.viscur.autotests.utils

import io.restassured.RestAssured
import ru.viscur.autotests.restApiResources.Endpoints

class Helpers {

    companion object {

        fun getQueInfo() : String  = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().get(Endpoints.QUE_INFO).
                then().statusCode(200).extract().body().asString()

        fun makeRef(id: String, type: String): String = "{\"reference\": \"$type/$id\", \"type\": \"$type\"}"

        fun deleteQue() = RestAssured.given().auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").
                `when`().delete(Endpoints.QUE_DELETE_ALL).then().statusCode(200)

    }
}