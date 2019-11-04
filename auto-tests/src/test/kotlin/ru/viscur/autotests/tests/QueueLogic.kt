package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.Helpers.Companion.createObservation
import ru.viscur.autotests.utils.Helpers.Companion.createPatientResource
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.valueSets.BundleType

@Disabled("Debug purposes only")
class QueueLogic {

    companion object {
        //фельдшер
        val paramedicId = Helpers.paramedicId
        //кто делает все observation
        val diagnosticAssistantId = Helpers.diagnosticAssistantId
        //ответсвенный
        val respPractitionerId = Helpers.surgeonId
    }

    @Test
    fun addingObservation() {

        /*val observation = Observation(
                status = ObservationStatus.final,
                issued = now(),
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

    @Test
    fun patientsUziSorting() {
        val office116 = referenceToLocation("Office:116")
        val office117 = referenceToLocation("Office:117")

        // uziPatient1
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle0 = bundle("1111","RED", listOf(servReq1))

        //uziPatient2
        val servReq2 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle3 = bundle("1112","RED", listOf(servReq2))

        QueRequests.deleteQue()
        QueRequests.cabinetIsBusy(office116)
        QueRequests.cabinetIsBusy(office117)
        val servReqUzi1 = QueRequests.createPatient(bundle0).extract().response().`as`(Bundle::class.java).entry.first().resource as ServiceRequest
        val servReqUzi2 = QueRequests.createPatient(bundle3).extract().response().`as`(Bundle::class.java).entry.first().resource as ServiceRequest
        val queitem117 = QueRequests.getOfficeQue(office117).extract().response().`as`(Bundle::class.java).entry.first().resource as QueueItem
        val queitem116 = QueRequests.getOfficeQue(office116).extract().response().`as`(Bundle::class.java).entry.first().resource as QueueItem

        //проверка, что оба идут на обследование в разных кабинетах
        assertEquals(servReqUzi1.subject?.id, queitem117.subject.id, "patient ${servReqUzi1.subject?.id} in wrong que")
        assertEquals(servReqUzi2.subject?.id, queitem116.subject.id, "patient ${servReqUzi2.subject?.id} in wrong que")
    }

    @Test
    fun forceInvite() {
        val office117 = referenceToLocation("Office:117")
        val office139 = referenceToLocation("Office:139")
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle = bundle("1113", "RED", listOf(servReq1))

        QueRequests.deleteQue()
        QueRequests.officeIsReady(referenceToLocation("Office:117"))
        val actServReq = QueRequests.createPatient(bundle).extract().response().`as`(Bundle::class.java).entry.first().resource as ServiceRequest
        val patientId = actServReq.subject?.id!!
        val queitem117 = QueRequests.getOfficeQue(office117).extract().response().`as`(Bundle::class.java).entry.first().resource as QueueItem

        //проверка, что пациент стоит в очереди в 117
        assertEquals(patientId, queitem117.subject.id, "patient $patientId in wrong que")

        val forceInviteList = createListResource(patientId,"Office:139")
        QueRequests.invitePatientToOffice(forceInviteList)
        val queitem139 = QueRequests.getOfficeQue(office139).extract().response().`as`(Bundle::class.java).entry.first().resource as QueueItem

        //проверка, что пациента вызвали в 139
        assertEquals(patientId, queitem139.subject.id, "patient $patientId in wrong que")
    }
}