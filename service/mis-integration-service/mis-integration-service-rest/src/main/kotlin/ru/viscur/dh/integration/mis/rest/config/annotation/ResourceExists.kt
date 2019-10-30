package ru.viscur.dh.integration.mis.rest.config.annotation

import ru.viscur.dh.integration.mis.rest.config.validation.*
import javax.validation.*
import kotlin.reflect.*

/**
 * Аннотация для валидации ресурса на существование в системе (по id)
 */
@Constraint(validatedBy = [ExistsByIdValidator::class])
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResourceExists(
        val message: String = "Resource with such id does not exist",
        val groups: Array<KClass<out Any>> = [],
        val payload: Array<KClass<out Payload>> = []
)
