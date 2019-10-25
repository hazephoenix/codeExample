package ru.viscur.dh.integration.mis.rest

import org.springframework.http.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.queue.api.*
import java.lang.Exception

/**
 * Контроллер для осмотра (examination) пациентов ответственным врачом
 */
@RestController
@RequestMapping("/examination")
@Validated
class ExaminationController(
        private val patientService: PatientService,
        private val carePlanService: CarePlanService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val serviceRequestService: ServiceRequestService,
        private val queueManagerService: QueueManagerService
) {
    /**
     * Получить список активных пациентов ответсвенного врача
     */
    @GetMapping("/patients")
    fun getActiveByPractitioner(@RequestParam practitionerId: String) =
        Bundle(entry = carePlanService.activeByPractitioner(practitionerId).map { BundleEntry(it) })

    /**
     * Назначить дообследование пациенту
     */
    @PostMapping("/serviceRequests")
    fun addServiceRequests(@RequestBody bundle: Bundle, @RequestParam patientId: String) {
        try {
            val patient = patientService.byId(patientId)
            serviceRequestService.add(patient.id, bundle.entry.map { it.resource as ServiceRequest })
            queueManagerService.deleteFromOfficeQueue(patient.id)
            queueManagerService.calcServiceRequestExecOrders(patient.id)
            queueManagerService.addToOfficeQueue(patient.id)
        } catch (exception: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Active CarePlan or Patient not found")
        }
    }

    /**
     * Завершить осмотр пациента
     */
    @PostMapping
    fun finishExamination(@RequestBody bundle: Bundle) = clinicalImpressionService.finish(bundle)
}