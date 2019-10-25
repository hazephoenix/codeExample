package ru.viscur.dh.integration.mis.rest

import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

/**
 * Общий контроллер для работы с ресурсами
 */
@RestController
class ResourceController(private val resourceService: ResourceService) {
    /**
     * Получить ресурс по его id
     */
    @GetMapping("{resourceTypeId}/{id}")
    fun getResource(@PathVariable resourceTypeId: ResourceType.ResourceTypeId, @PathVariable id: String): BaseResource? {
        try {
            return resourceService.byId(ResourceType.byId(resourceTypeId), id)
        } catch (exception: Exception){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Resource can not be found")
        }
    }
}