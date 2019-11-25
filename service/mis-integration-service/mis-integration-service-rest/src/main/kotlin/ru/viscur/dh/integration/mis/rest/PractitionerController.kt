package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.fhir.model.entity.Practitioner

/**
 * Created at 25.10.2019 11:11 by SherbakovaMA
 *
 * Контроллер для работы со справочником мед. персонала
 */
@RestController
@RequestMapping("/practitioner")
class PractitionerController(
        private val practitionerService: PractitionerService
) {

    /**
     * see [PractitionerService.all]
     */
    @GetMapping
    fun practitioners(@RequestParam(required = false) withBlocked: Boolean = false) = practitionerService.all(withBlocked)

    /**
     * see [PractitionerService.byId]
     */
    @GetMapping("byId")
    fun practitioner(@RequestParam id: String) = practitionerService.byId(id)

    /**
     * see [PractitionerService.create]
     */
    @PostMapping
    fun create(@RequestBody practitioner: Practitioner) = practitionerService.create(practitioner)

    /**
     * see [PractitionerService.update]
     */
    @PutMapping
    fun update(@RequestBody practitioner: Practitioner) = practitionerService.update(practitioner)

    /**
     * see [PractitionerService.updateBlocked]
     */
    @PostMapping("blocked")
    fun updateBlocked(
            @RequestParam practitionerId: String,
            @RequestParam value: Boolean
    ) = practitionerService.updateBlocked(practitionerId, value)
}