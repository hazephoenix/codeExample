package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.DiagnosticReportStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId

/**
 * Created at 01.10.2019 13:33 by SherbakovaMA
 *
 * Заключение (основной диагноз)
 * [info](http://fhir-ru.github.io/diagnosticreport.html)
 *
 * @param subject пациент, ссылка на [Patient]
 * @param performer кто исполнил (ссылка на [Practitioner], [Organization])
 * @param conclusionCode кодируемый тип заключения фельдшера (коды медицинских заключений полученных результатов)
 * @param conclusion строковая интерпретация заключения
 * @param result измерения (множество полученных результатов измерений). ссылки на [Observation]
 * @param status status статус (частичный/предварительный/окончательный)
 */
class DiagnosticReport @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType = ResourceType.DiagnosticReport,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("performer") val performer: List<Reference>,
        @JsonProperty("conclusion") val conclusion: String? = null,
        @JsonProperty("conclusionCode") val conclusionCode: List<CodeableConcept>,
//        @JsonProperty("result") val result: List<Reference>,
        @JsonProperty("status") val status: DiagnosticReportStatus = DiagnosticReportStatus.registered
) : BaseResource(id, identifier, resourceType)