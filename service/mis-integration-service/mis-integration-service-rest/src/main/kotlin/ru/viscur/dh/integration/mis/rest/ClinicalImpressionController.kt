package ru.viscur.dh.integration.mis.rest

import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*

/**
 * Контроллер для работы с обращениями пациентов
 * (для ответсвенного врача)
 *
 * TODO: переименовать endpoint?
 */
@RestController
@RequestMapping("/ClinicalImpression")
@Validated
class ClinicalImpressionController(
        private val carePlanService: CarePlanService,
        private val clinicalImpressionService: ClinicalImpressionService
) {
    /**
     * Получить список маршрутных листов ответсвенного врача TODO
     */
    @GetMapping
    fun getActiveByPractitioner(@RequestParam practitionerId: String) =
        Bundle(entry = carePlanService.getActiveByPractitioner(practitionerId).map { BundleEntry(it) })

    /**
     * Завершить прием (обращение) пациента
     */
    @PostMapping("finish")
    fun finishClinicalImpression(@RequestBody bundle: Bundle) = clinicalImpressionService.finish(bundle)
}