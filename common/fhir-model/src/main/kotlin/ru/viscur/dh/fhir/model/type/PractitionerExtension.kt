package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 23.11.2019 12:12 by SherbakovaMA
 *
 * Доп. поля для мед. работника [ru.viscur.dh.fhir.model.entity.Practitioner]
 *
 * @param blocked заблокирован
 */
class PractitionerExtension @JsonCreator constructor(
        @JsonProperty("blocked") var blocked: Boolean = false
)