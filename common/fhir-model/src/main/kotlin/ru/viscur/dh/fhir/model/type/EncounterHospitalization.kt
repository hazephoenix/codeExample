package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 02.10.2019 18:45 by SherbakovaMA
 *
 * Сведения о госпитализации в медицинском учреждении во взаимодействии [ru.viscur.dh.fhir.model.entity.Encounter]
 *
 * @param destination место/организация куда госпитализируется пациент, ссылка на [ru.viscur.dh.fhir.model.entity.Location]
 * @param dischargeDisposition категория или вид места размещения после выписки
 * @param specialArrangement носилки/переводчик/инвалидная коляска и т.д.
 */
class EncounterHospitalization @JsonCreator constructor(
        @JsonProperty("destination") val destination: Reference,
        @JsonProperty("dischargeDisposition") val dischargeDisposition: CodeableConcept? = null,
        @JsonProperty("specialArrangement") val specialArrangement: List<CodeableConcept>? = null
)