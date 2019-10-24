package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.queue.api.QueueManagerService

@RestController
@RequestMapping("/mis-integration")
class MisIntegrationController(
        private val queueManagerService: QueueManagerService
) {

    @GetMapping("/hello")
    fun hello() = "Hello world!"


    /**
     * Доназначения обследований пациенту
     */
    @PostMapping("/addServiceRequests")
    fun predictServiceRequests(@RequestBody bundle: Bundle) {
//        todo добавить в careplan
        val patientId = "12"
        queueManagerService.deleteFromOfficeQueue(patientId)
        queueManagerService.calcServiceRequestExecOrders(patientId)
        queueManagerService.addToOfficeQueue(patientId)
    }
}