package ru.viscur.dh.fhir.model.enums

import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.entity.Bundle

/**
 * Created at 03.10.2019 10:30 by SherbakovaMA
 *
 * Используемые типы ресурсов
 */
enum class ResourceType(val clazz: Class<out BaseResource>) {
    Bundle(ru.viscur.dh.fhir.model.entity.Bundle::class.java),
    CarePlan(ru.viscur.dh.fhir.model.entity.CarePlan::class.java),
    CareTeam(ru.viscur.dh.fhir.model.entity.CareTeam::class.java),
    ChargeItem(ru.viscur.dh.fhir.model.entity.ChargeItem::class.java),
    Claim(ru.viscur.dh.fhir.model.entity.Claim::class.java),
    ClinicalImpression(ru.viscur.dh.fhir.model.entity.ClinicalImpression::class.java),
    Concept(ru.viscur.dh.fhir.model.entity.Concept::class.java),
    Consent(ru.viscur.dh.fhir.model.entity.Consent::class.java),
    DiagnosticReport(ru.viscur.dh.fhir.model.entity.DiagnosticReport::class.java),
    Encounter(ru.viscur.dh.fhir.model.entity.Encounter::class.java),
    HealthcareService(ru.viscur.dh.fhir.model.entity.HealthcareService::class.java),
    ListResource(ru.viscur.dh.fhir.model.entity.ListResource::class.java),
    Location(ru.viscur.dh.fhir.model.entity.Location::class.java),
    Observation(ru.viscur.dh.fhir.model.entity.Observation::class.java),
    Organization(ru.viscur.dh.fhir.model.entity.Organization::class.java),
    Patient(ru.viscur.dh.fhir.model.entity.Patient::class.java),
    Practitioner(ru.viscur.dh.fhir.model.entity.Practitioner::class.java),
    PractitionerRole(ru.viscur.dh.fhir.model.entity.PractitionerRole::class.java),
    Procedure(ru.viscur.dh.fhir.model.entity.Procedure::class.java),
    Questionnaire(ru.viscur.dh.fhir.model.entity.Questionnaire::class.java),
    QuestionnaireResponse(ru.viscur.dh.fhir.model.entity.QuestionnaireResponse::class.java),
    ServiceRequest(ru.viscur.dh.fhir.model.entity.ServiceRequest::class.java),
    Specimen(ru.viscur.dh.fhir.model.entity.Specimen::class.java),
    ValueSet(ru.viscur.dh.fhir.model.entity.ValueSet::class.java)
}