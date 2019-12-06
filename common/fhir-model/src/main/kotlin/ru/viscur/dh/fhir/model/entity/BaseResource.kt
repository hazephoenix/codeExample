package ru.viscur.dh.fhir.model.entity

import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.ResourceMeta
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 01.10.2019 12:57 by SherbakovaMA
 *
 * Базовый ресурс
 *
 * @param id id ресурса
 * @param identifier идентификаторы ресурса, [Identifier]
 * @param resourceType тип ресурса
 * @param meta мета-информация о ресурсе (версионность)
 */
abstract class BaseResource(
        var id: String = genId(),
        var identifier: List<Identifier>? = null,
        val resourceType: ResourceType.ResourceTypeId,
        var meta: ResourceMeta = ResourceMeta()
)