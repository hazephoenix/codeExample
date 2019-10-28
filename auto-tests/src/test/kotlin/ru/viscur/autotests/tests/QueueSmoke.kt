package ru.viscur.autotests.tests

import org.slf4j.LoggerFactory
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.autotests.data.RequestsData
import org.assertj.core.api.Assertions.assertThat
import ru.viscur.autotests.restApiResources.QueRequests
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.ListResourceEntry
import ru.viscur.dh.fhir.model.type.Reference

@EnableAutoConfiguration
class QueueSmoke {

    companion object {
        private val log = LoggerFactory.getLogger(QueueSmoke::class.java)
    }
    val patientRef = Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = RequestsData.red1)

    @Test
    @Order(2)
    fun patientShouldBeDeletedFromQue() {
        QueRequests.deleteQue()
        QueRequests.addPatientToQue(patientRef)
        QueRequests.deletePatientFromQue(patientRef)
        assertThat(QueRequests.getQueInfo()).doesNotContain(RequestsData.red1)
    }

    @Test
    @Order(1)
    fun patientShouldBeAddedToQue() {
        QueRequests.deleteQue()
        QueRequests.addPatientToQue(patientRef)
        assertThat(QueRequests.getQueInfo()).contains(RequestsData.red1)
    }

    @Test
    fun patientShouldBeInvitedToOffice() {
        val expectedQueInfo = "queue for Office:130 (WAITING_PATIENT):\n" +
                "<br/>  QueueItem(0, 0, GOING_TO_OBSERVATION, GREEN, subject=Ref(reference='Patient/35bc92de-c55c-45da-9821-32be6bb23396'"
        val invitePatientTestBody = ListResource(
                entry = listOf(
                    ListResourceEntry(Reference(
                            resourceType = ResourceType.ResourceTypeId.Patient,id = RequestsData.green2)
                    ),
                    ListResourceEntry(Reference(
                            resourceType = ResourceType.ResourceTypeId.Location,id = "Office:130")
                    )
                )
        )
        QueRequests.deleteQue()
        QueRequests.invitePatientToOffice(invitePatientTestBody)
        assertThat(QueRequests.getQueInfo()).contains(expectedQueInfo)
    }

}

