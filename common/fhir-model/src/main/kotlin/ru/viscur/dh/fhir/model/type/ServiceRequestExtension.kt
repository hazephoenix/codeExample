package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.*

/**
 * Дополнительные поля для услуги
 *
 * @param executionOrder порядок выполнения услуги
 */
class ServiceRequestExtension(
    @JsonProperty("executionOrder") var executionOrder: Int
)