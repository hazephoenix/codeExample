package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.CodeMapService
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.valueSetNameById
import ru.viscur.dh.queue.api.OfficeService

/**
 * Created at 25.10.2019 11:11 by SherbakovaMA
 *
 * Контроллер для справочной информации (ValueSet, CodeMap и т.д.)
 */
@RestController
@RequestMapping("/dictionary")
class DictionaryController(
        private val codeMapService: CodeMapService,
        private val officeService: OfficeService,
        private val conceptService: ConceptService,
        private val resourceService: ResourceService
) {

    /**
     * Содержимое справочника [ru.viscur.dh.fhir.model.entity.ValueSet] указанного [parentCode]
     * Если [parentCode] не указан, то корневые
     */
    @GetMapping("/{valueSetId}")
    fun concepts(
            @PathVariable valueSetId: String,
            @RequestParam parentCode: String?
    ) = conceptService.byParent(valueSetNameById(valueSetId), parentCode)

    /**
     * see [CodeMapService.allIcdToPractitionerQualifications]
     */
    @GetMapping("/icdToPractitionerQualifications")
    fun allIcdToPractitionerQualifications() = codeMapService.allIcdToPractitionerQualifications()

    /**
     * see [CodeMapService.allIcdToObservationTypes]
     */
    @GetMapping("/icdToObservationTypes")
    fun allIcdToObservationTypes() = codeMapService.allIcdToObservationTypes()

    /**
     * see [CodeMapService.allRespQualificationToObservationTypes]
     */
    @GetMapping("/respQualificationToObservationTypes")
    fun allRespQualificationToObservationTypes() = codeMapService.allRespQualificationToObservationTypes()

    /**
     * Все кабинеты
     */
    @GetMapping("/offices")
    fun offices() = officeService.all()

    @GetMapping("/organizations")
    fun organizations() = resourceService.all(ResourceType.Organization)
}