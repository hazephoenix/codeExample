package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Created at 13.11.2019 16:34 by SherbakovaMA
 *
 * Доп. поля для [ru.viscur.dh.fhir.model.entity.ClinicalImpression]
 *
 * @param severity степень тяжести пациента на текущее обращение
 */
class ClinicalImpressionExtension @JsonCreator constructor(
        @JsonProperty("severity") var severity: Severity
)