package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class ServiceRequests {

    companion object {
        val office101 = "Office:101"
        val office139 = "Office:139"
    }

    @Test
    fun responseShouldReturnServiceRequests () {
        val diagnosis = " {\"diagnosis\": \"A01\",\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"],\"gender\": \"male\"}"
        QueRequests.getSupposedServRequests(diagnosis).
                assertThat().body("entry.size()", equalTo(6))
    }

    @Test
    fun serviceRequestAdding() {
        val observationOfSurgeonCode = "СтХир"
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observationOfSurgeonCode)
        )
        val bundle = Helpers.bundle("7879", Severity.RED.toString(), servRequests)
        val responseBundle = QueRequests.createPatient(bundle)
        val patientId = patientIdFromServiceRequests(responseBundle.resources(ResourceType.ServiceRequest))

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139)
        ))

        val additionalServiceRequests = listOf(
                Helpers.createServiceRequestResource("B03.016.002ГМУ_СП", patientId)
        )
        val bundleForExamin = Bundle(
                entry = additionalServiceRequests.map { BundleEntry(it) }
        )
        val updatedCarePlan = QueRequests.addServiceRequests(bundleForExamin)

        //в CarePlan должен быть добавлен новый ServiceRequest
        assertEquals(2, updatedCarePlan.activity.size, "wrong care plan activities")

        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = "СтХир", locationId = office139),
                ServiceRequestInfo(code = "B03.016.002ГМУ_СП", locationId = office101)
        ))
    }
}