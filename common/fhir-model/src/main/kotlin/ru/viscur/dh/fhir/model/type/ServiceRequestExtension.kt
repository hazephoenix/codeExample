package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.*
import java.util.*

/**
 * Дополнительные поля для услуги
 *
 * @param executionOrder порядок выполнения услуги
 * @param execStart время начала выполнения услуги
 * @param execEnd время окончания выполнения услуги
 */
class ServiceRequestExtension @JsonCreator constructor(
    @JsonProperty("executionOrder") var executionOrder: Int = 0,
    @JsonProperty("execStart") var execStart: Date? = null,
    @JsonProperty("execEnd") var execEnd: Date? = null
)