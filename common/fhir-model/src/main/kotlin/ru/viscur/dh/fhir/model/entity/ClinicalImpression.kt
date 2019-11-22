package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ClinicalImpressionStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.ClinicalImpressionExtension
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*

/**
 * Клиническая оценка состояния больного
 * при обращении
 * Фактически описывает одно обращение пациента
 * [info](http://fhir-ru.github.io/clinicalimpression.html)
 *
 * @param status [ClinicalImpressionStatus] статус
 * @param date дата (и время) обращения (дата регистрации)
 * @param subject пациент, ссылка на [Patient]
 * @param assessor отв. врач, ссылка на [Practitioner]
 * @param supportingInfo список ссылок на все относящееся к одному этому обращение в приемное отделение, ссылки на [Observation], [Claim], [CarePlan], [Consent], [QuestionnaireResponse], [Encounter], [DiagnosticReport] и др
 * @param summary заключение на основании проведенной оценки
 * @param extension доп поля, [ClinicalImpressionExtension]
 */
class ClinicalImpression @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.ClinicalImpression.id,
        @JsonProperty("status") var status: ClinicalImpressionStatus,
        @JsonProperty("date") val date: Date,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("assessor") val assessor: Reference,
        @JsonProperty("supportingInfo") var supportingInfo: List<Reference>,
        @JsonProperty("summary") val summary: String,
        @JsonProperty("extension") val extension: ClinicalImpressionExtension
) : BaseResource(id, identifier, resourceType)