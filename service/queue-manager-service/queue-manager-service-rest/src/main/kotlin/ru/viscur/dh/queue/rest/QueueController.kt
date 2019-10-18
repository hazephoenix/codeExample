package ru.viscur.dh.queue.rest

import org.springframework.web.bind.annotation.*
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
    fun test () = "Test"

    @PostMapping("/registerPatient")
    fun registerPatient(@RequestParam patientReference: Reference) =
            logAndValidateAfter { queueManagerService.registerPatient(patientReference.id!!) }

    @PostMapping("/office/patientEntered")
    fun patientEntered(

            @RequestParam patientId: String,
            @RequestParam officeId: String
    ) = logAndValidateAfter { queueManagerService.patientEntered(patientId, officeId) }

    @PostMapping("/office/patientLeft")
    fun patientLeft(@RequestParam officeId: String) =
            logAndValidateAfter { queueManagerService.patientLeft(officeId) }

    @PostMapping("/office/ready")
    fun officeIsReady(@RequestParam officeId: String) =
            logAndValidateAfter { queueManagerService.officeIsReady(officeId) }

    @PostMapping("/office/busy")
    fun officeIsBusy(@RequestParam officeId: String) =
            logAndValidateAfter { queueManagerService.officeIsBusy(officeId) }

    @PostMapping("/office/closed")
    fun officeIsClosed(@RequestParam officeId: String) =
            logAndValidateAfter { queueManagerService.officeIsClosed(officeId) }

    @PostMapping("/patient/addToQueue")
    fun addToOfficeQueue(
            @RequestParam patientId: String
    ) = logAndValidateAfter { queueManagerService.addToOfficeQueue(patientId) }

    @DeleteMapping("/patient")
    fun patientLeftQueue(
            @RequestParam patientId: String
    ) = logAndValidateAfter { queueManagerService.deleteFromOfficeQueue(patientId) }


//    @GetMapping("/surveys")
//    fun notVisitedSurveys(@RequestParam surveyTypeId: String) =
//            queueManagerService.routeSheets.flatMap { it.surveys }.filter { it.surveyType.id == surveyTypeId && !it.visited}

    @DeleteMapping
    fun deleteQueue() = logAndValidateAfter { queueManagerService.deleteQueue()
        "deleted"}

    @DeleteMapping("/history")
    fun deleteHistory() = logAndValidateAfter { queueManagerService.deleteHistory() }

    private fun <T> logAndValidateAfter(body: () -> T): T {
        val result = body()
        queueManagerService.loqAndValidate()
        return result
    }
}
