package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.dh.fhir.model.enums.ResourceType

@Disabled("Debug purposes only")
class ResourceById {

    @Test
    fun gettingLocationResourceById() {
        //запрос ресурса Location
        val location = QueRequests.resource(ResourceType.Location, "Office:101")

        //проверка, что в ответе соответствующий ресурс
        assertEquals("Office:101", location.id, "wrong resourse in response")
    }
}