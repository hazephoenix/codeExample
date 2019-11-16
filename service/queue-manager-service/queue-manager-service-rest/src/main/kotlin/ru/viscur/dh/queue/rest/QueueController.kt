package ru.viscur.dh.queue.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.BundleEntry
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 11.09.2019 9:39 by SherbakovaMA
 *
 * Контроллер для очереди
 */
@RestController
@RequestMapping("/queue")
class QueueController(private val queueManagerService: QueueManagerService) {

    @GetMapping("recalcNextOffice")
    fun needRecalcNextOffice() = queueManagerService.needRecalcNextOffice()

    @PostMapping("recalcNextOffice")
    fun recalcNextOffice(@RequestParam value: Boolean) = queueManagerService.recalcNextOffice(value)

    @PostMapping("/office/patientEntered")
    fun patientEntered(@RequestBody listOfReference: ListResource) = logAndValidateAfter {
        val serviceRequests = queueManagerService.patientEntered(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id!!,
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id!!
        )
        Bundle(entry = serviceRequests.map { BundleEntry(it) })
    }

    @PostMapping("/office/forceSendPatientToObservation")
    fun forceSendPatientToObservation(@RequestBody listOfReference: ListResource) = logAndValidateAfter {
        queueManagerService.forceSendPatientToObservation(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id!!,
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id!!
        )
    }

    @PostMapping("/office/setAsFirst")
    fun setAsFirst(@RequestBody listOfReference: ListResource) = logAndValidateAfter {
        queueManagerService.setAsFirst(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id!!,
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id!!
        )
    }

    @PostMapping("/office/patientLeft")
    fun patientLeft(@RequestBody listOfReference: ListResource) = logAndValidateAfter {
        queueManagerService.patientLeft(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id!!,
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id!!
        )
    }

    @PostMapping("/office/cancelEntering")
    fun cancelEntering(@RequestBody patientReference: Reference) =
            logAndValidateAfter { queueManagerService.cancelEntering(patientReference.id!!) }

    @PostMapping("/office/ready")
    fun officeIsReady(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.officeIsReady(officeReference.id!!) }

    @PostMapping("/office/nextPatient")
    fun enterNextPatient(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.enterNextPatient(officeReference.id!!) }

    @PostMapping("/office/busy")
    fun officeIsBusy(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.officeIsBusy(officeReference.id!!) }

    @PostMapping("/office/closed")
    fun officeIsClosed(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.officeIsClosed(officeReference.id!!) }

    @PostMapping("/patient/addToQueue")
    fun addToOfficeQueue(
            @RequestBody patientReference: Reference
    ) = logAndValidateAfter { queueManagerService.addToQueue(patientReference.id!!) }

    @DeleteMapping("/patient")
    fun patientLeftQueue(
            @RequestBody patientReference: Reference
    ) = logAndValidateAfter { queueManagerService.deleteFromQueue(patientReference.id!!) }

    @DeleteMapping
    fun deleteQueue() = logAndValidateAfter {
        queueManagerService.deleteQueue()
        "deleted"
    }

    @DeleteMapping("/history")
    fun deleteHistory() = logAndValidateAfter { queueManagerService.deleteHistory() }

    @GetMapping
    fun queueOfOffice(@RequestBody officeReference: Reference) = queueManagerService.queueOfOffice(officeReference.id!!)

    @GetMapping("/queueItems")
    fun queueItems() = queueManagerService.queueItems()

    @GetMapping("/locationMonitor")
    fun locationMonitor(@RequestParam officeId: String) = queueManagerService.locationMonitor(officeId)

    @GetMapping("/info")
    fun queueInfo() = queueManagerService.loqAndValidate()

    private fun <T> logAndValidateAfter(body: () -> T): T {
        val result = body()
        queueManagerService.loqAndValidate()
        return result
    }
}
