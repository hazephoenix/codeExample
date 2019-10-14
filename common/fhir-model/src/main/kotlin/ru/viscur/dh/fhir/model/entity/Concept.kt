package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 03.10.2019 15:42 by SherbakovaMA
 *
 * Код справочника [ValueSet]
 *
 * @param code код
 * @param parentCode код родителя
 * @param display отображаемое название
 * @param system ссылка на справочник, к которому принадлежит код, совпадает с [ValueSet.url], например, "ValueSet/ICD-10"
 * @param priority приоритет выполнения услуги в очереди (0..1)
 * @param additionalInfo дополнительная информация
 */
class Concept @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Concept.id,
        @JsonProperty("code") val code: String,
        @JsonProperty("parentCode") val parentCode: String? = null,
        @JsonProperty("display") val display: String,
        @JsonProperty("system") val system: String,
        @JsonProperty("priority") val priority: Double? = null,
        @JsonProperty("additionalInfo") val additionalInfo: String? = null
) : BaseResource(id, identifier, resourceType)