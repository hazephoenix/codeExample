package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.DiagnosticReport
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import java.sql.*
import java.util.Date

/**
 * Created at 15.10.2019 11:52 by SherbakovaMA
 *
 * todo
 */
@Service
class PatientServiceImpl(
        private val resourceService: ResourceService
) : PatientService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun byId(id: String): Patient =
            resourceService.byId(ResourceType.Patient, id)

    override fun severity(patientId: String): Severity {
        //находим clinicalImpression пациента со статусом active
        //у него находим questionnaireResponse по Questionnaire/Severity_criteria
        //у него находим ответ item по linkId = Severity, из него берем answer-valueCoding-code
        val q = em.createNativeQuery("""
            select jsonb_array_elements(items.item -> 'answer') -> 'valueCoding' ->> 'code' as severity
            from (
                     select jsonb_array_elements(r.resource -> 'item') as item
                     from questionnaireResponse r
                     where r.resource ->> 'questionnaire' = 'Questionnaire/Severity_criteria'
                       and 'QuestionnaireResponse/' || r.id in (
                         select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                         from clinicalImpression ci
                         where ci.resource -> 'subject' ->> 'reference' = :patientRef
                            and ci.resource ->> 'status' = 'active'
                     )
                 ) as items
            where items.item ->> 'linkId' = 'Severity'
        """.trimIndent())
        q.setParameter("patientRef", "Patient/$patientId")
        val severityStr = (q.singleResult as Array<Any>)[0] as String
        return enumValueOf(severityStr)
    }

    override fun serviceRequests(patientId: String): List<ServiceRequest> {
        //active clinicalImpression -> carePlan -> all serviceRequests
        val query = em.createNativeQuery("""
            select sr.resource
            from serviceRequest sr
            where 'ServiceRequest/' || sr.id in (
                select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from carePlan cp
                where 'CarePlan/' || cp.id in (
                    select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                    from clinicalImpression ci
                    where ci.resource -> 'subject' ->> 'reference' = :patientRef
                      and ci.resource ->> 'status' = 'active'
                )
            )
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResourceList()
    }

    override fun activeServiceRequests(patientId: String): List<ServiceRequest> {
        //active clinicalImpression -> carePlan -> active serviceRequests
        val query = em.createNativeQuery("""
            select sr.resource
            from serviceRequest sr
            where 'ServiceRequest/' || sr.id in (
                select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from carePlan cp
                where 'CarePlan/' || cp.id in (
                    select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                    from clinicalImpression ci
                    where ci.resource -> 'subject' ->> 'reference' = :patientRef
                      and ci.resource ->> 'status' = 'active'
                )
            )
            and sr.resource->>'status' = 'active'
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResourceList()
    }

    override fun queueStatusOfPatient(patientId: String): PatientQueueStatus {
        val patient = byId(patientId)
        return patient.extension.queueStatus
    }

    override fun preliminaryDiagnosticConclusion(patientId: String): String? {
        val query = em.createNativeQuery("""
            select r.resource
            from diagnosticReport r
            where r.resource ->> 'status' = 'preliminary'
              and 'DiagnosticReport/' || r.id in (
                select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                from clinicalImpression ci
                where ci.resource -> 'subject' ->> 'reference' = :patientRef
                  and ci.resource ->> 'status' = 'active'
            )""")
        query.setParameter("patientRef", "Patient/$patientId")
        val diagnosticReport = query.fetchResource<DiagnosticReport>()
        return diagnosticReport?.conclusionCode?.first()?.coding?.first()?.code
    }

    override fun saveFinalPatientData(bundle: Bundle): String {
        val resources = bundle.entry.map { resourceService.create(it.resource)!! }
        val date = Date()
        val diagnosticReport = getResources<DiagnosticReport>(resources, ResourceType.ResourceTypeId.DiagnosticReport).first()
        val paramedicReference = diagnosticReport.performer.first()
        //todo проверять создан ли уже. как его идентифицировать?.. если создан, то обновить
        val patient = getResources<Patient>(resources, ResourceType.ResourceTypeId.Patient).first()
        val serviceRequests = getResources<ServiceRequest>(resources, ResourceType.ResourceTypeId.ServiceRequest)
        val patientReference = Reference(patient)
        CarePlan(
                subject = patientReference,
                author = paramedicReference,
                contributor = paramedicReference,
                status = CarePlanStatus.active,
                created = Timestamp(date.time),
                title = "Маршрутный лист",
                activity = serviceRequests
                        .map { CarePlanActivity(outcomeReference = Reference(it)) }
        ).let { resourceService.create(it)!! }
        val encounter = Encounter(subject = patientReference).let { resourceService.create(it)!! }
        ClinicalImpression(
                status = ClinicalImpressionStatus.completed,
                date = date,
                subject = patientReference,
                assessor = paramedicReference,
                summary = "Заключение: ${diagnosticReport.conclusion}",
                encounter = Reference(encounter)
        ).let { resourceService.create(it)!! }
        return patient.id!!
    }

    private fun <T> getResources(resources: List<BaseResource>, type: ResourceType.ResourceTypeId): List<T> where T : BaseResource {
        return resources.filter { it.resourceType == type }.map { it as T }
    }
}