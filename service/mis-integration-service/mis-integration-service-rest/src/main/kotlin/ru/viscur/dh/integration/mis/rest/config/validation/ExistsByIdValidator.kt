package ru.viscur.dh.integration.mis.rest.config.validation

import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.integration.mis.rest.config.annotation.*
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * Валидатор наличия ресурса с таким id в БД (datastorage)
 */
class ExistsByIdValidator(
        private val resourceService: ResourceService
) : ConstraintValidator<ResourceExists, BaseResource> {

    override fun isValid(value: BaseResource, context: ConstraintValidatorContext?): Boolean {
        try {
            resourceService.byId(ResourceType.byId(value.resourceType), value.id)
        } catch (exception: Exception) {
            return false
        }
        return true
    }
}