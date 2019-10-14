package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 04.10.2019 10:44 by SherbakovaMA
 *
 * Назначение в [плане назначений CarePlan][ru.viscur.dh.fhir.model.entity.CarePlan]
 *
 * @param outcomeReference процедура, ссылка на [ru.viscur.dh.fhir.model.entity.ServiceRequest]
 * @param detail детальное описание, [CarePlanActivityDetail]
 */
class CarePlanActivity @JsonCreator constructor(
        @JsonProperty("outcomeReference") val outcomeReference: Reference,
        @JsonProperty("detail") val detail: CarePlanActivityDetail? = null
)