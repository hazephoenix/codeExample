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
 * @param queueCode отображаемый код для очереди. Например: З-12/Ж-122 - степень тяжести + №п/п от начала смены
 * @param forBandageOnly обращение выполняется только для выполнения перевязки
 */
class ClinicalImpressionExtension @JsonCreator constructor(
        @JsonProperty("severity") var severity: Severity,
        @JsonProperty("queueCode") val queueCode: String,
        @JsonProperty("forBandageOnly") val forBandageOnly: Boolean? = null
)