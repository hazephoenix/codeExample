package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 01.10.2019 14:37 by SherbakovaMA
 *
 * Доп. информация обращения
 *
 * @param valueString значение в формате строки
 * @param valueBoolean значение в формате Булево
 * @param valueInteger значение в формате Целое
 * todo очень много полей. нужно добавить только необходимые
 */
class ClaimSupportingInfo @JsonCreator constructor(
        @JsonProperty("valueString") val valueString: String? = null,
        @JsonProperty("valueBoolean") val valueBoolean: Boolean? = null,
        @JsonProperty("valueInteger") val valueInteger: Int? = null

)