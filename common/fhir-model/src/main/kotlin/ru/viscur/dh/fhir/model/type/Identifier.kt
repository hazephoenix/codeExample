package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.IdentifierUse
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 01.10.2019 12:56 by SherbakovaMA
 *
 * Идентификатор
 *
 * @param value значение, являющееся уникальным
 * @param period срок действия
 * @param type тип
 * @param use статус использования
 * @param assigner организация, назначившая идентификатор, ссылка на [ru.viscur.dh.fhir.model.entity.Organization] или просто текст в [Reference.display]
 *   для идентификатора пациента (паспорта): кем выдан [ru.viscur.dh.fhir.model.entity.Organization.name] и код подразделения [ru.viscur.dh.fhir.model.entity.Organization.identifier]
 */
class Identifier @JsonCreator constructor(
        @JsonProperty("value") val value: String,
        @JsonProperty("period") val period: Period? = null,
        @JsonProperty("type") val type: CodeableConcept,
        @JsonProperty("use") val use: IdentifierUse = IdentifierUse.official,
        @JsonProperty("assigner") val assigner: Reference? = null
) {
    constructor(value: String, type: IdentifierType) : this(value = value, type = CodeableConcept(code = type.toString(), systemId = ValueSetName.IDENTIFIER_TYPES.id))
}