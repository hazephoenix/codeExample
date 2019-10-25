package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 01.10.2019 14:28 by SherbakovaMA
 *
 * Концепт, имеющий код в одной или нескольких системах кодирования
 *
 * @param coding коды, указывающие как описан концепт (в каких системах кодирования)
 * @param text текстовое представление концепта
 */
class CodeableConcept @JsonCreator constructor(
        @JsonProperty("coding") val coding: List<Coding>,
        @JsonProperty("text") val text: String? = null
) {
    constructor(code: String, systemId: String, display: String? = null) :
            this(coding = listOf(Coding(code = code, system = "ValueSet/$systemId", display = display)))

    constructor(code: String, system: ValueSetName, display: String? = null) :
            this(coding = listOf(Coding(code = code, system = "ValueSet/${system.id}", display = display)))
}