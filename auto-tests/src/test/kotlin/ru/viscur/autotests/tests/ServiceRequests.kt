package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApiResources.QueRequests

class ServiceRequests {


    @Test
    fun responseShouldContainReturnServiceRequests () {
        val diagnosis = " {\"diagnosis\": \"A01\",\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"],\"gender\": \"male\"}"
        QueRequests.getServiceRequests(diagnosis).log().all().
                assertThat().body("entry.size()", equalTo(6))
    }
}