package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*

/**
 * Ресурс для связи кодов справочников
 *
 * Используется для связи кодов специальностей и услуг, кодов МКБ-10 и специальностей и т.д.
 *
 * @property sourceUrl ссылка на исходный справочник
 * @property targetUrl ссылка на связываемый справочник
 * @property sourceCode код в исходном справочнике
 * @property targetCode код(ы) в связываемом справочнике
 */
class CodeMap @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.CodeMap.id,
        @JsonProperty("sourceUrl") val sourceUrl: String,
        @JsonProperty("targetUrl") val targetUrl: String,
        @JsonProperty("sourceCode") val sourceCode: String,
        @JsonProperty("targetCode") var targetCode: List<CodeMapTargetCode>
) : BaseResource(id, identifier, resourceType)