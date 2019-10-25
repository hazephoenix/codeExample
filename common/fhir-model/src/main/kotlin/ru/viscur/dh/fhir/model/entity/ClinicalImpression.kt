package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ClinicalImpressionStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*

/**
 * Клиническая оценка состояния больного на основании
 * первичного осмотра и измерений
 *
 * @param status: [ClinicalImpressionStatus] статус
 * @param date: дата (и время) вынесения решения
 * @param subject: [Patient]
 * @param assessor: [Practitioner]
 * @param investigation: список наблюдений (измерения, опросники, процедуры, диагнозы и т.д.),
 *  на основании которых выносится заключение
 *  @param supportingInfo список ссылок на все относящееся к одному этому обращение в приемное отделение, ссылки на [Observation], [Claim], [CarePlan], [Consent], [QuestionnaireResponse], [Encounter], [DiagnosticReport] и др
 * @param summary: Заключение на основании проведенной оценки
 * @param encounter: [Encounter]
 */
class ClinicalImpression @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.ClinicalImpression.id,
        @JsonProperty("status") var status: ClinicalImpressionStatus,
        @JsonProperty("date") val date: Date,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("assessor") val assessor: Reference,
        @JsonProperty("investigation") val investigation: List<Any>? = null,
        @JsonProperty("supportingInfo") val supportingInfo: List<Reference>,
        @JsonProperty("summary") val summary: String,
        @JsonProperty("encounter") val encounter: Reference? = null
) : BaseResource(id, identifier, resourceType)