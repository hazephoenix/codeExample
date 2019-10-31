package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApiResources.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.valueSets.*

class Test2 {

    @Test
    @Order(1)
    fun patientE2e () {
        val patient = Helpers.createPatientResource("7879")
        val bodyWeight = Helpers.createObservation("Weight", 90, Reference(patient))
        val questionnaireResponseSeverityCriteria = Helpers.createQuestResponse(Reference(patient))
        val personalDataConsent = Helpers.createConsent(Reference(patient))
        val diagnosticReport = Helpers.createDiagnosticReport("A00.0", Reference(patient))
        val list = Helpers.createPractitionerListResource("хирург_Петров")
        val claim = Helpers.createClaim(Reference(patient))
        val servReq1 = Helpers.createServiceRequest("СтХир", Reference(patient))

        val bundle = Bundle(
                type = BundleType.BATCH.value,
                entry = listOf(
                        BundleEntry(patient),
                        BundleEntry(diagnosticReport),
                        BundleEntry(bodyWeight),
                        BundleEntry(servReq1),
                        BundleEntry(personalDataConsent),
                        BundleEntry(list),
                        BundleEntry(claim),
                        BundleEntry(questionnaireResponseSeverityCriteria)
                )
        )
        QueRequests.deleteQue()

        val office139Id = "Office:139"
        QueRequests.getCabinetBusy(referenceToLocation(office139Id))
        val responseBundle = QueRequests.createPatient(bundle).extract().response().`as`(Bundle::class.java)

        //проверка количества и наличия Service Request
        assertEquals(1, responseBundle.entry.size, "number of servicerequests in response")
        assertEquals(ResourceType.ServiceRequest.id, responseBundle.entry.first().resource.resourceType, "")

        //проверка что есть первый Service Request и он в нужный кабинет
        val actServiceRequest = responseBundle.entry.get(0).resource as ServiceRequest
        assertNotNull(actServiceRequest.subject?.id, "id patient")
        assertEquals(office139Id, actServiceRequest.locationReference?.first()?.id, "code ... ")

        val patientId = actServiceRequest.subject?.id!!
        assertEquals(servReq1.code.code(), actServiceRequest.code.code(), "code ... ")

        var actPatient = QueRequests.getResource(ResourceType.Patient.id.toString(), patientId).extract().response().`as`(Patient::class.java)
        assertEquals(PatientQueueStatus.IN_QUEUE, actPatient.extension.queueStatus, "wrong status of patient ... ")

        //queue items..
        val patientEnteredListResource = ListResource(
                entry = listOf(
                        ListResourceEntry(
                                item = Reference(resourceType = ResourceType.ResourceTypeId.Patient, id = patientId)
                        ),
                        ListResourceEntry(
                                item = Reference(resourceType = ResourceType.ResourceTypeId.Location, id = office139Id)
                        )
                )
        )
        /*val patientEnteredListResource = Helpers.createPatientAndLocationListResource(Reference(patient), referenceToLocation(office139Id))*/
        
        QueRequests.getCabinetRdy(referenceToLocation(office139Id))
        val actOfficeServiceList = QueRequests.patientEntered(patientEnteredListResource).extract().response().`as`(Bundle::class.java)
        actPatient = QueRequests.getResource(ResourceType.Patient.id.toString(), patientId).extract().response().`as`(Patient::class.java)
        //проверка что в кабинете необходимые обследования и пациент
        assertEquals(1, actOfficeServiceList.entry.size, "wrong number of office's service requests")
        assertEquals(PatientQueueStatus.ON_OBSERVATION, actPatient.extension.queueStatus, "wrong patient status")


//
//        responseBundle.entry.find { it as Location
//
//        }
//
//        listOf(Location(), Location()).filter{
//            name = "" + ""
//            it.name == name
//        }
//        val names = listOf(Location(), Location()).map {
//            it.name
//        }
    }
}