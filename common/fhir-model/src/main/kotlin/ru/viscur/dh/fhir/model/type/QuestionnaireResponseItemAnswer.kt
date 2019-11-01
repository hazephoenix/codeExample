package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import java.util.*

/**
 * Created at 02.10.2019 17:40 by SherbakovaMA
 *
 * Значение ответа в пункте ответа [QuestionnaireResponseItem]
 * Заполняется только одно значение в value*
 *
 * @param item вложенные группы и вопросы
 */
class QuestionnaireResponseItemAnswer @JsonCreator constructor(
        @JsonProperty("valueBoolean") val valueBoolean: Boolean? = null,
        @JsonProperty("valueDecimal") val valueDecimal: Double? = null,
        @JsonProperty("valueInteger") val valueInteger: Int? = null,
        @JsonProperty("valueDate") val valueDate: Date? = null,
        @JsonProperty("valueDateTime") val valueDateTime: Date? = null,
        @JsonProperty("valueTime") val valueTime: Date? = null,
        @JsonProperty("valueString") val valueString: String? = null,
        @JsonProperty("valueAttachment") val valueAttachment: Attachment? = null,
        @JsonProperty("valueCoding") val valueCoding: Coding? = null,
        @JsonProperty("valueQuantity") val valueQuantity: Quantity? = null,
        @JsonProperty("valueSampledData") val valueSampledData: SampledData? = null,
        @JsonProperty("valueRegerence") val valueRegerence: Reference? = null,
        @JsonProperty("item") val item: List<QuestionnaireResponseItem>? = null
)