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
    fun recalcNextOffice(@RequestParam value: Boolean) {
        queueManagerService.recalcNextOffice(value)
    }

    @PostMapping("/office/patientEntered")
    fun patientEntered(@RequestBody listOfReference: ListResource) =
            queueManagerService.patientEntered(
                    listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id(),
                    listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id()
            ).let { serviceRequests -> Bundle(entry = serviceRequests.map { BundleEntry(it) }) }

    @PostMapping("/office/forceSendPatientToObservation")
    fun forceSendPatientToObservation(@RequestBody listOfReference: ListResource) =
            queueManagerService.forceSendPatientToObservation(
                    listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id(),
                    listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id()
            )

    @PostMapping("/office/setAsFirst")
    fun setAsFirst(@RequestBody listOfReference: ListResource) {
        queueManagerService.setAsFirst(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id(),
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id()
        )
    }

    @PostMapping("/office/delayGoingToObservation")
    fun delayGoingToObservation(@RequestBody patientReference: Reference) {
        queueManagerService.delayGoingToObservation(patientId = patientReference.id(), onlyIfFirstInQueueIsLongWaiting = false)
    }

    @PostMapping("/office/patientLeft")
    fun patientLeft(@RequestBody listOfReference: ListResource) {
        queueManagerService.patientLeft(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id(),
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id()
        )
    }

    @PostMapping("/office/cancelEntering")
    fun cancelEntering(@RequestBody patientReference: Reference) {
        queueManagerService.cancelEntering(patientReference.id())
    }

    @PostMapping("/office/ready")
    fun officeIsReady(@RequestBody officeReference: Reference) {
        queueManagerService.officeIsReady(officeReference.id())
    }

    @PostMapping("/office/nextPatient")
    fun enterNextPatient(@RequestBody officeReference: Reference) {
        queueManagerService.enterNextPatient(officeReference.id())
    }

    @PostMapping("/office/busy")
    fun officeIsBusy(@RequestBody officeReference: Reference) {
        queueManagerService.officeIsBusy(officeReference.id())
    }

    @PostMapping("/office/closed")
    fun officeIsClosed(@RequestBody officeReference: Reference) {
        queueManagerService.officeIsClosed(officeReference.id())
    }

    @PostMapping("/patient/addToQueue")
    fun addToOfficeQueue(@RequestBody patientReference: Reference) {
        queueManagerService.addToQueue(patientReference.id())
    }

    @DeleteMapping("/patient")
    fun patientLeftQueue(@RequestBody patientReference: Reference) {
        queueManagerService.deleteFromQueue(patientReference.id())
    }

    @DeleteMapping
    fun deleteQueue() {
        queueManagerService.deleteQueue()
    }

    @DeleteMapping("/history")
    fun deleteHistory() {
        queueManagerService.deleteHistory()
    }

    @GetMapping
    fun queueOfOffice(@RequestBody officeReference: Reference) = queueManagerService.queueOfOffice(officeReference.id())

    @GetMapping("/queueItems")
    fun queueItems() = queueManagerService.queueItems()

    @GetMapping("/locationMonitor")
    fun locationMonitor(@RequestParam officeId: String) = queueManagerService.locationMonitor(officeId)

    @GetMapping("/info")
    fun queueInfo() = queueManagerService.loqAndValidate()
}
