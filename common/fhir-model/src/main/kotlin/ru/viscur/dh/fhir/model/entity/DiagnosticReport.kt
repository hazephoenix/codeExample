package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.DiagnosticReportStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*

/**
 * Created at 01.10.2019 13:33 by SherbakovaMA
 *
 * Заключение (основной диагноз)
 * [info](http://fhir-ru.github.io/diagnosticreport.html)
 *
 * @param subject пациент, ссылка на [Patient]
 * @param performer кто исполнил (ссылка на [Practitioner], [Organization])
 * @param conclusionCode кодируемый тип заключения (коды медицинских заключений полученных результатов)
 * @param conclusion строковая интерпретация заключения
 * @param result измерения (множество полученных результатов измерений). ссылки на [Observation]
 * @param issued дата и время принятия решения
 * @param status status статус (частичный/предварительный/окончательный)
 */
class DiagnosticReport @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.DiagnosticReport.id,
        @JsonProperty("subject") var subject: Reference,
        @JsonProperty("performer") val performer: List<Reference>,
        @JsonProperty("conclusion") val conclusion: String? = null,
        @JsonProperty("conclusionCode") val conclusionCode: List<CodeableConcept>,
        @JsonProperty("issued") val issued: Date,
//        @JsonProperty("result") val result: List<Reference>,
        @JsonProperty("status") val status: DiagnosticReportStatus = DiagnosticReportStatus.preliminary
) : BaseResource(id, identifier, resourceType)