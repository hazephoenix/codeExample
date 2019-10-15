package ru.viscur.dh.fhir.model.enums

import ru.viscur.dh.fhir.model.entity.*
import java.lang.IllegalStateException

/**
 * Created at 03.10.2019 10:30 by SherbakovaMA
 *
 * Используемые типы ресурсов
 */

class ResourceType<TEntityClass>
private constructor(
        val id: ResourceTypeId,
        val entityClass: Class<TEntityClass>
) {
    companion object {
        @JvmStatic private  val typesByIdString = mutableMapOf<String, ResourceType<out BaseResource>>()

        val Bundle = createType<Bundle>(ResourceTypeId.Bundle)
        val CarePlan = createType<CarePlan>(ResourceTypeId.CarePlan)
        val CareTeam = createType<CareTeam>(ResourceTypeId.CareTeam)
        val ChargeItem = createType<ChargeItem>(ResourceTypeId.ChargeItem)
        val Claim = createType<Claim>(ResourceTypeId.Claim)
        val ClinicalImpression = createType<ClinicalImpression>(ResourceTypeId.ClinicalImpression)
        val Concept = createType<Concept>(ResourceTypeId.Concept)
        val Consent = createType<Consent>(ResourceTypeId.Consent)
        val DiagnosticReport = createType<DiagnosticReport>(ResourceTypeId.DiagnosticReport)
        val Encounter = createType<Encounter>(ResourceTypeId.Encounter)
        @JvmStatic val HealthcareService = createType<HealthcareService>(ResourceTypeId.HealthcareService)
        val ListResource = createType<ListResource>(ResourceTypeId.ListResource)
        val Location = createType<Location>(ResourceTypeId.Location)
        val Observation = createType<Observation>(ResourceTypeId.Observation)
        val Organization = createType<Organization>(ResourceTypeId.Organization)
        val Patient = createType<Patient>(ResourceTypeId.Patient)
        val Practitioner = createType<Practitioner>(ResourceTypeId.Practitioner)
        val PractitionerRole = createType<PractitionerRole>(ResourceTypeId.PractitionerRole)
        val Procedure = createType<Procedure>(ResourceTypeId.Procedure)
        val Questionnaire = createType<Questionnaire>(ResourceTypeId.Questionnaire)
        val QuestionnaireResponse = createType<QuestionnaireResponse>(ResourceTypeId.QuestionnaireResponse)
        val ServiceRequest = createType<ServiceRequest>(ResourceTypeId.ServiceRequest)
        val Specimen = createType<Specimen>(ResourceTypeId.Specimen)
        val ValueSet = createType<ValueSet>(ResourceTypeId.ValueSet)


        private inline fun <reified TEntityClass : BaseResource> createType(id: ResourceTypeId): ResourceType<TEntityClass> {
            val instance = ResourceType(id, TEntityClass::class.java);
            val key = id.toString()
            if (typesByIdString.containsKey(key)) {
                throw IllegalStateException("Duplicate id '$key' for a resource type")
            }
            typesByIdString[key] = instance
            return instance
        }

        fun byId(id: String): ResourceType<out BaseResource> = typesByIdString[id]
                ?: throw Exception("Unknown resource type '$id'")

        fun byId(id: ResourceTypeId) = byId(id.toString())

    }

    enum class ResourceTypeId {
        Bundle,
        CarePlan,
        CareTeam,
        ChargeItem,
        Claim,
        ClinicalImpression,
        Concept,
        Consent,
        DiagnosticReport,
        Encounter,
        HealthcareService,
        ListResource,
        Location,
        Observation,
        Organization,
        Patient,
        Practitioner,
        PractitionerRole,
        Procedure,
        Questionnaire,
        QuestionnaireResponse,
        ServiceRequest,
        Specimen,
        ValueSet
    }

}