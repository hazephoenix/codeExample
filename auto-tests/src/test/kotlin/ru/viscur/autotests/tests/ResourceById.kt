package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.OFFICE_101_ID
import ru.viscur.dh.fhir.model.enums.ResourceType

@Disabled("Debug purposes only")
class ResourceById {

    @Test
    fun gettingLocationResourceById() {
        //запрос ресурса Location
        val location = QueRequests.resource(ResourceType.Location, OFFICE_101_ID)

        //проверка, что в ответе соответствующий ресурс
        assertEquals(OFFICE_101_ID, location.id, "wrong resource in response")
    }
}