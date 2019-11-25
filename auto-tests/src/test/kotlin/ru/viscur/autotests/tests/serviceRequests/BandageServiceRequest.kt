package ru.viscur.autotests.tests.serviceRequests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.dto.QueueItemInfo
import ru.viscur.autotests.dto.QueueItemsOfOffice
import ru.viscur.autotests.dto.ServiceRequestInfo
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.checkQueueItems
import ru.viscur.autotests.utils.checkServiceRequestsOfPatient
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.utils.referenceToLocation
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class BandageServiceRequest {

    companion object{
        val bandageCode = "Перевязка"
        val office128 = "Office:128"
    }

    @BeforeEach
    fun prepare() {
        QueRequests.deleteQue()
        QueRequests.officeIsReady(referenceToLocation(office128))
    }

    @Test
    fun bandagePatientFullCycle() {
        //создание пациента на перевязку
        val bundle = Helpers.bundle("1119", "GREEN")
        val bandage = QueRequests.createBandagePatient(bundle)
        val patientId = patientIdFromServiceRequests(bandage.resources(ResourceType.ServiceRequest))

        //проверка, что у пациента только один Service Request
        checkServiceRequestsOfPatient(patientId, listOf(
                ServiceRequestInfo(code = bandageCode, locationId = "Office:128", status = ServiceRequestStatus.active)
        ))

        //проверка, что пациента отправляет в кабинет для перевязки
        checkQueueItems(listOf(
                QueueItemsOfOffice(office128, listOf(
                        QueueItemInfo(patientId, PatientQueueStatus.GOING_TO_OBSERVATION)
                ))
        ))

        //пациент вошел в кабинет
        val bandageServRequestId = QueRequests.patientEntered(Helpers.createListResource(patientId, office128)).first().id

        //перевязка выполнена
        val observationBandage = Helpers.createObservation(
                status = ObservationStatus.final,
                basedOnServiceRequestId = bandageServRequestId
        )
        QueRequests.createObservation(observationBandage)

        //проверка, что после перевязки у пациента больше активных Service Request и его нет в очереди
        checkServiceRequestsOfPatient(patientId, listOf())
        checkQueueItems(listOf())
    }
}