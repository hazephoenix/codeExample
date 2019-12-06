package ru.viscur.autotests.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import ru.viscur.autotests.dto.*
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.dh.fhir.model.dto.PatientToExamine
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.execDuration

/**
 * Проверка состояния очереди
 */
fun checkQueueItems(itemsByOffices: List<QueueItemsOfOffice>) {
    val actQueueItems = QueRequests.queueItems()
    val allItems = itemsByOffices.flatMap { it.items }
    val itemsStr = itemsToStr(itemsByOffices, actQueueItems)
    //количество в принципе разное
    Assertions.assertEquals(allItems.size, actQueueItems.size, "wrong number of queueItems. $itemsStr")
    itemsByOffices.forEach { byOffice ->
        val officeId = byOffice.officeId
        byOffice.items.forEachIndexed { index, queueItemInfo ->
            //поиск соответствующего элемента в текущих
            val foundInAct = actQueueItems.filter { it.subject.id == queueItemInfo.patientId && it.location.id == officeId }
            Assertions.assertEquals(1, foundInAct.size, "not found (or found multiple items) of $queueItemInfo. $itemsStr")
            val foundItem = foundInAct.first()
            //проверка правильности данных в найденном
            Assertions.assertEquals(index, foundItem.onum, "wrong onum of $queueItemInfo. $itemsStr")
            val actPatient = QueRequests.resource(ResourceType.Patient, queueItemInfo.patientId)
            Assertions.assertEquals(queueItemInfo.status, actPatient.extension.queueStatus, "wrong status of patient with id ${queueItemInfo.patientId}. $itemsStr")
            //правильность статуса кабинета в зависимости от статуса первого пациента в очереди
            /*if (index == 0) {
                val actOffice = QueRequests.resource(ResourceType.Location, officeId)
                val expPatientStatus = when (actOffice.status) {
                    LocationStatus.CLOSED, LocationStatus.BUSY -> PatientQueueStatus.IN_QUEUE
                    LocationStatus.OBSERVATION -> PatientQueueStatus.ON_OBSERVATION
                    LocationStatus.WAITING_PATIENT -> PatientQueueStatus.GOING_TO_OBSERVATION
                    else -> throw Exception("wrong status '${actOffice.status}' of office with id ${actOffice.id}. must be one of (CLOSED, BUSY, OBSERVATION, WAITING_PATIENT)")
                }
                Assertions.assertEquals(expPatientStatus, actPatient.extension.queueStatus,
                        "not proper status of office ${actOffice.id} ($expPatientStatus) to first patient in queue status: ${actPatient.extension.queueStatus}")
            }*/
        }
    }
}

private fun itemsToStr(itemsByOffices: List<QueueItemsOfOffice>, actQueueItems: List<QueueItem>): String {
    actQueueItems.forEach {
        it.apply {
            val patient = QueRequests.resource(ResourceType.Patient, subject.id)
            patientQueueStatus = patient.extension.queueStatus
        }
    }
    val actByOffices = actQueueItems.groupBy { it.location.id }
    return "\n\nexp queue:\n" +
            itemsByOffices.joinToString("\n") { byOffice -> byOffice.officeId + ":\n  " + byOffice.items.mapIndexed { index, queueItemInfo -> "$index. $queueItemInfo" }.joinToString("\n  ") } +
            "\n\nactual queue:\n" +
            actByOffices.map { (officeId, items) ->
                val office = QueRequests.resource(ResourceType.Location, officeId)
                officeId + " (${office.status}):\n  " + items.sortedBy { it.onum }.joinToString("\n  ")
            }.joinToString("\n  ") +
            "\n\n"
}

/**
 * Проверка пациентов в ответсвтенности врачей
 */
fun checkPatientsOfResp(patientsInfo: List<PatientsOfRespInfo>) {
    val actPatientsOfResp = QueRequests.patientsOfResp()
    val allPatients = patientsInfo.flatMap { it.patientsInfo }
    val patientsStr = patientsOfRespToString(patientsInfo, actPatientsOfResp)
    //количество в принципе разное
    Assertions.assertEquals(allPatients.size, actPatientsOfResp.size, "wrong number of patients. $patientsStr")
    patientsInfo.forEach { patientsInfoOfPractitioner ->
        patientsInfoOfPractitioner.patientsInfo.forEach { patientInfo ->
            //поиск соответствующего элемента в текущих
            val foundInAct = actPatientsOfResp.filter { it.patientId == patientInfo.patientId && it.practitionerId == patientsInfoOfPractitioner.practitionerId }
            Assertions.assertEquals(1, foundInAct.size, "not found (or found multiple items) of $patientsInfoOfPractitioner. $patientsStr")
            val foundItem = foundInAct.first()
            //проверка правильности данных в найденном
            Assertions.assertEquals(patientInfo.severity.toString(), foundItem.severity, "wrong severity of $patientInfo. $patientsStr")
        }
    }
}

fun patientsOfRespToString(patientsInfo: List<PatientsOfRespInfo>, actPatientsOfResp: List<PatientToExamine>): String {
    val actByPractitioners = actPatientsOfResp.sortedBy { it.practitionerId }.groupBy { it.practitionerId }
    return "\n\nexp: \n  " +
            patientsInfo.sortedBy { it.practitionerId }.map { "resp '${it.practitionerId}':\n  " + it.patientsInfo.joinToString("\n  ") { it.toString() } } +
            "\n\nactual :\n  " +
            actByPractitioners.map { (practitionerId, patients) ->
                "resp '$practitionerId':\n  " + patients.joinToString("\n  ") { it.toString() }
            } + "\n\n"
}

/**
 * Проверка правильности назначений определенного пациента
 * (не полная проверка назначений всех пациентов, а только в разрезе одного пациента)
 */
fun checkServiceRequestsOfPatient(patientId: String, servReqInfos: List<ServiceRequestInfo>) {
    val actServRequests = QueRequests.serviceRequestsOfPatients(patientId)
    compareServiceRequests(patientId, servReqInfos, actServRequests)
}

/**
 * Сравнивает 2 списка назначений: [servReqInfos] ожидаемый и [actServRequests] текущий
 * пациента [patientId]
 */
fun compareServiceRequests(patientId: String, servReqInfos: List<ServiceRequestInfo>, actServRequests: List<ServiceRequest>) {
    val servReqsStr = servReqsToString(patientId, servReqInfos, actServRequests)
    //количество в принципе разное
    Assertions.assertEquals(servReqInfos.size, actServRequests.size, "wrong number of servRequests. $servReqsStr")
    servReqInfos.forEach { servReqInfo ->
        val foundInAct = actServRequests.filter { it.code.code() == servReqInfo.code }
        Assertions.assertEquals(1, foundInAct.size, "not found (or found multiple items) with code '${servReqInfo.code}' of $servReqInfo. $servReqsStr")
        val foundItem = foundInAct.first()
        //проверка правильности данных в найденном
        Assertions.assertEquals(patientId, foundItem.subject?.id, "wrong patientId of $servReqInfo. $servReqsStr")
        Assertions.assertEquals(servReqInfo.status, foundItem.status, "wrong status of $servReqInfo. $servReqsStr")
        Assertions.assertEquals(servReqInfo.locationId, foundItem.locationReference?.first()?.id, "wrong locationId of $servReqInfo. $servReqsStr")
        servReqInfo.execDuration?.run {
            assertEquals(servReqInfo.execDuration, foundItem.extension?.execDuration(), "wrong execDuration of $servReqInfo. $servReqsStr")
        }
    }
}

fun servReqsToString(patientId: String, servReqInfos: List<ServiceRequestInfo>, actServRequests: List<ServiceRequest>): String {
    return "\n\nfor patient '$patientId'\nexp servRequests:\n  " +
            servReqInfos.joinToString("\n  ") { it.toString() } +
            "\n\nactual:\n  " +
            actServRequests.joinToString("\n  ") { "code: " + it.code.code() + ", status: " + it.status + ", locationId: " + it.locationReference?.first()?.id } +
            "\n\n"
}

/**
 * Проверка правильности обследований определенного пациента
 * (не полная проверка обследований всех пациентов, а только в разрезе одного пациента)
 */
fun checkObservationsOfPatient(patientId: String, observationInfos: List<ObservationInfo>) {
    val actObservations = QueRequests.observations(patientId)
    val observationsStr = observationsToString(patientId, observationInfos, actObservations)
    //количество в принципе разное
    Assertions.assertEquals(observationInfos.size, actObservations.size, "wrong number of observations. $observationsStr")
    observationInfos.forEach { observationInfo ->
        val foundInAct = actObservations.filter { it.code.code() == observationInfo.code }
        Assertions.assertEquals(1, foundInAct.size, "not found (or found multiple items) with code '${observationInfo.code}' of $observationInfo. $observationsStr")
        val foundItem = foundInAct.first()
        //проверка правильности данных в найденном
        Assertions.assertEquals(patientId, foundItem.subject.id, "wrong patientId of $observationInfo. $observationsStr")
        Assertions.assertEquals(observationInfo.status, foundItem.status, "wrong status of $observationInfo. $observationsStr")
        Assertions.assertEquals(observationInfo.basedOnId, foundItem.basedOn?.id, "wrong basedOn of $observationInfo. $observationsStr")
        Assertions.assertEquals(observationInfo.valueInt, foundItem.valueInteger, "wrong valueInteger of $observationInfo. $observationsStr")
        Assertions.assertEquals(observationInfo.valueStr, foundItem.valueString, "wrong valueInteger of $observationInfo. $observationsStr")
    }
}

fun observationsToString(patientId: String, servReqInfos: List<ObservationInfo>, actServRequests: List<Observation>): String {
    return "\n\nfor patient '$patientId'\nexp observations:\n  " +
            servReqInfos.joinToString("\n  ") { it.toString() } +
            "\n\nactual:\n  " +
            actServRequests.joinToString("\n  ") {
                "code: " + it.code.code() + ", status: " + it.status + ", basedOn: " + it.basedOn?.id +
                        ", valueInt: " + it.valueInteger + ", valueStr: " + it.valueString
            } + "\n\n"
}

fun patientIdFromServiceRequests(serviceRequestsFromResponse: List<ServiceRequest>): String {
    Assertions.assertTrue(serviceRequestsFromResponse.size > 0, "list of service requests can't be empty")
    Assertions.assertNotNull(serviceRequestsFromResponse.first().subject?.id, "wrong id patient")
    return serviceRequestsFromResponse.first().subject?.id
}