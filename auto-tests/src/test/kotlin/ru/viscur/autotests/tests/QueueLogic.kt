package ru.viscur.autotests.tests

import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createListResource
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.utils.referenceToLocation

//@Disabled("Debug purposes only")
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
        val office116 = "Office:116"
        val office117 = "Office:117"

        //uziPatient1
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle1 = bundle("1111","RED", listOf(servReq1))

        //uziPatient2
        val servReq2 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle2 = bundle("1112","RED", listOf(servReq2))

        //добавление 2 пациентов на узи
        QueRequests.deleteQue()
        QueRequests.officeIsBusy(referenceToLocation(office116))
        QueRequests.officeIsBusy(referenceToLocation(office117))
        val servReqUzi1 = QueRequests.createPatient(bundle1).entry.first().resource as ServiceRequest
        val patientId1 = servReqUzi1.subject!!.id!!
        val servReqUzi2 = QueRequests.createPatient(bundle2).entry.first().resource as ServiceRequest
        val patientId2 = servReqUzi2.subject!!.id!!

        //проверка что оба пациента в очереди в разные кабинеты узи
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId1, PatientQueueStatus.IN_QUEUE)
                )),
                QueueItemsOfOffice(office117, listOf(
                        QueueItemInfo(patientId2, PatientQueueStatus.IN_QUEUE)
                ))
        ))
    }

    @Test
    fun patientForceInviteToOffice() {
        val office116 = "Office:116"
        val office139 = "Office:139"
        val servReq1 = Helpers.createServiceRequestResource("A04.16.001")
        val bundle = bundle("1113", "RED", listOf(servReq1))

        QueRequests.deleteQue()
        QueRequests.officeIsReady(referenceToLocation(office116))
        val actServReq = QueRequests.createPatient(bundle).entry.first().resource as ServiceRequest
        val patientId = actServReq.subject?.id!!

        //проверка, что пациент стоит в очереди в 116
        checkQueueItems(listOf(
                QueueItemsOfOffice(office116, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        QueRequests.officeIsBusy(referenceToLocation(office139))
        QueRequests.invitePatientToOffice(createListResource(patientId,office139))

        //проверка, что пациента вызвали в 139
        checkQueueItems(listOf(
                QueueItemsOfOffice(office139, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))
    }
}