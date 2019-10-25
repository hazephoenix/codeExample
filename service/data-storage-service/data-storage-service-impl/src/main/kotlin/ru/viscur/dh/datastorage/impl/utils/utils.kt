package ru.viscur.dh.datastorage.impl.utils

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

fun <T> getResourcesFromList(resources: List<BaseResource>, type: ResourceType.ResourceTypeId): List<T> where T : BaseResource {
    return resources.filter { it.resourceType == type }.map { it as T }
}