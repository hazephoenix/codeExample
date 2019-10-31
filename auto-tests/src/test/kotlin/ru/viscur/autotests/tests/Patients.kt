package ru.viscur.autotests.tests

import org.junit.jupiter.api.Test
import ru.viscur.autotests.data.TestData
import ru.viscur.autotests.restApiResources.QueRequests
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.ListResourceEntry
import ru.viscur.dh.fhir.model.type.Reference
import java.sql.Timestamp
import java.time.LocalDateTime

class Patients {
    @Test
    fun patientShouldBeOnObservation() {
        //create Patient first
        /*val list = ListResource(
                entry = listOf(
                        ListResourceEntry(
                                Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = TestData.testRed)
                        ),
                        ListResourceEntry(
                                Reference(resourceType = ResourceType.ResourceTypeId.Location, id = "Office:101")
                        )
                )
        )

        QueRequests.createPatient(Helpers.jsonToString("TestRed.json")).log().all()
        QueRequests.deleteQue()
        QueRequests.getCabinetRdy(Reference(resourceType = ResourceType.ResourceTypeId.Location, id = "Office:101"))
        QueRequests.addPatientToQue(Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = TestData.testRed))
        QueRequests.patientEntered(list)*/
    }

    @Test
    fun addingObservation() {

        /*val observation = Observation(
                status = ObservationStatus.final,
                issued = Timestamp.valueOf(LocalDateTime.now()),
                basedOn = Reference(
                        resourceType = ResourceType.ResourceTypeId.ServiceRequest,
                        id = "dd252a96-a5f1-4206-9e00-f0e9a0ec716a"
                ),
                performer = listOf(
                        Reference(
                                resourceType = ResourceType.ResourceTypeId.Practitioner, id = "80a3c463-3d68-4aa6-8020-de2a90c92962"
                        )
                ),
                code = CodeableConcept(
                        systemId = "ValueSet/Observation_types",
                        code = "B03.016.002ГМУ_СП"
                ),
                subject = Reference(
                        resourceType = ResourceType.ResourceTypeId.Patient, id = "c1d7e69a-3845-4a55-9c89-548d3a5c0114"
                ),
                valueString = "asdf"
        )
        QueRequests.createObservation(observation).log().all()*/
    }
}