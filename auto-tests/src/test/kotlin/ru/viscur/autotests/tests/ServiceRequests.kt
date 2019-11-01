package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests

@Disabled("Debug purposes only")
class ServiceRequests {


    @Test
    fun responseShouldContainReturnServiceRequests () {
        val diagnosis = " {\"diagnosis\": \"A01\",\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"],\"gender\": \"male\"}"
        QueRequests.getSupposedServRequests(diagnosis).log().all().
                assertThat().body("entry.size()", equalTo(6))

    }
}