package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.jackson.BundleEntryDeserializer

/**
 * Элемент списка ресурсов в контейнере [ru.viscur.dh.fhir.model.entity.Bundle]
 *
 * @param resource Ресурс
 */
@JsonDeserialize(using = BundleEntryDeserializer::class)
data class BundleEntry @JsonCreator constructor(
        @JsonProperty("resource") val resource: BaseResource
)