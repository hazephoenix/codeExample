package ru.viscur.dh.queue.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.fhir.model.entity.ListResource
import ru.viscur.dh.fhir.model.enums.ResourceType
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

    @GetMapping("/test")
    fun test() = "Test"

    @PostMapping("/registerPatient")
    fun registerPatient(@RequestBody patientReference: Reference) =
            logAndValidateAfter { queueManagerService.registerPatient(patientReference.id!!) }

    @PostMapping("/office/patientEntered")
    fun patientEntered(@RequestBody listOfReference: ListResource) = logAndValidateAfter {
        queueManagerService.patientEntered(
                listOfReference.entry.first { it.item.type == ResourceType.Patient.id }.item.id!!,
                listOfReference.entry.first { it.item.type == ResourceType.Location.id }.item.id!!
        )
    }

    @PostMapping("/office/patientLeft")
    fun patientLeft(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.patientLeft(officeReference.id!!) }

    @PostMapping("/office/ready")
    fun officeIsReady(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.officeIsReady(officeReference.id!!) }

    @PostMapping("/office/busy")
    fun officeIsBusy(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.officeIsBusy(officeReference.id!!) }

    @PostMapping("/office/closed")
    fun officeIsClosed(@RequestBody officeReference: Reference) =
            logAndValidateAfter { queueManagerService.officeIsClosed(officeReference.id!!) }

    @PostMapping("/patient/addToQueue")
    fun addToOfficeQueue(
            @RequestParam patientId: String
    ) = logAndValidateAfter { queueManagerService.addToOfficeQueue(patientId) }

    @DeleteMapping("/patient")
    fun patientLeftQueue(
            @RequestBody patientReference: Reference
    ) = logAndValidateAfter { queueManagerService.deleteFromOfficeQueue(patientReference.id!!) }

    @DeleteMapping
    fun deleteQueue() = logAndValidateAfter {
        queueManagerService.deleteQueue()
        "deleted"
    }

    @DeleteMapping("/history")
    fun deleteHistory() = logAndValidateAfter { queueManagerService.deleteHistory() }

    private fun <T> logAndValidateAfter(body: () -> T): T {
        val result = body()
        queueManagerService.loqAndValidate()
        return result
    }
}
