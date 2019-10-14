package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.CarePlanActivityDetailStatus

/**
 * Created at 04.10.2019 10:47 by SherbakovaMA
 *
 * Детальное описание в [назначении CarePlanActivity][CarePlanActivity]
 *
 * @param status статус
 * @param code тип назначения
 * @param performer исполнитель, ссылка на [ru.viscur.dh.fhir.model.entity.Practitioner]
 */
class CarePlanActivityDetail @JsonCreator constructor(
        @JsonProperty("status") val status: CarePlanActivityDetailStatus = CarePlanActivityDetailStatus.in_progress,
        @JsonProperty("code") val code: CodeableConcept? = null,
        @JsonProperty("performer") val performer: List<Reference>
)