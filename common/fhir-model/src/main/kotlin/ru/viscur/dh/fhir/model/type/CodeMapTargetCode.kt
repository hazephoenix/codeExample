package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 23.10.2019 9:43 by SherbakovaMA
 *
 * Описание связываемого кода в [ru.viscur.dh.fhir.model.entity.CodeMap]
 *
 * @param code код
 * @param condition условия при котором учитывается сопоставление в [ru.viscur.dh.fhir.model.entity.CodeMap], коды
 */
class CodeMapTargetCode @JsonCreator constructor(
        @JsonProperty("code") val code: String,
        @JsonProperty("condition") val condition: List<Coding>? = listOf()
)