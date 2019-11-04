package ru.viscur.dh.integration.mis.rest

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.ClinicalImpressionService
import ru.viscur.dh.datastorage.api.ObservationService
import ru.viscur.dh.fhir.model.entity.Observation
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.integration.mis.rest.config.annotation.ResourceExists

/**
 * Контроллер для работы с обследованиями
 */
@RestController
@RequestMapping("/Observation")
@Validated
class ObservationController(
        private val observationService: ObservationService,
        private val clinicalImpressionService: ClinicalImpressionService
) {
    /**
     * Получить обследование по статусу и id пациента
     */
    @GetMapping
    fun getByPatientAndStatus(@RequestParam patientId: String, @RequestParam status: ObservationStatus? = null) =
            observationService.byPatientAndStatus(patientId, status)

    /**
     * Создать обследование
     */
    @PostMapping
    fun create(@RequestBody observation: Observation): Observation? {
        val patientId = patientIdByObservation(observation)
        return observationService.create(patientId, observation)
    }

    /**
     * Обновить обследование
     */
    @PutMapping
    fun update(@RequestBody @ResourceExists observation: Observation): Observation {
        val patientId = patientIdByObservation(observation)
        return observationService.update(patientId, observation)
    }

    private fun patientIdByObservation(observation: Observation): String = clinicalImpressionService.byServiceRequest(
            observation.basedOn?.id
                    ?: throw Exception("Error. Not defined serviceRequestId in basedOn field of Observation with id = '${observation.id}'")
    ).let {
        it.subject.id
                ?: throw Exception("Error. Not defined patientId in subject field of ClinicalImpression with id = '${it.id}'")
    }
}