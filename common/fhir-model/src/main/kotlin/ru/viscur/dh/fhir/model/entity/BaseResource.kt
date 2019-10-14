package ru.viscur.dh.fhir.model.entity

import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 01.10.2019 12:57 by SherbakovaMA
 *
 * Базовый ресурс
 *
 * @param identifier описание id ресурса
 * @param resourceType тип ресурса
 */
abstract class BaseResource(
        val id: String? = genId(),
        val identifier: List<Identifier>? = null,
        val resourceType: ResourceType
)