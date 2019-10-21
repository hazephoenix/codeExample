package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.CarePlanStatus
import ru.viscur.dh.fhir.model.enums.ClinicalImpressionStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.CarePlanActivity
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import java.sql.Timestamp
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 15.10.2019 11:52 by SherbakovaMA
 */
@Service
class PatientServiceImpl(
        private val resourceService: ResourceService,
        private val locationService: LocationService
) : PatientService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun byId(id: String): Patient = resourceService.byId(ResourceType.Patient, id)

    override fun byEnp(value: String): Patient? = resourceService.byIdentifier(ResourceType.Patient, IdentifierType.ENP, value)

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
        val severityStr = q.singleResult as String
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
        return patient.extension.queueStatus!!
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

    @Tx
    override fun saveFinalPatientData(bundle: Bundle): String {
        val resources = bundle.entry.map { it.resource.apply { id = genId() } }
        var patient = getResources<Patient>(resources, ResourceType.ResourceTypeId.Patient).first()
        val patientEnp = patient.identifier?.find { it.type.code() == IdentifierType.ENP.toString() }?.value
        val patientByEnp = patientEnp?.let { byEnp(patientEnp) }
        //нашли по ЕНП - обновляем, нет - создаем
        patient = patientByEnp?.let { byEnp -> resourceService.update(patient.apply { id = byEnp.id }) }
                ?: let { resourceService.create(patient) }

        cancelActiveClinicalImpression(patient.id)//todo может лучше сообщать о том, что есть активное обращение?

        val patientReference = Reference(patient)
        val diagnosticReport = getResources<DiagnosticReport>(resources, ResourceType.ResourceTypeId.DiagnosticReport).first().let { resourceService.create(it) }
        val paramedicReference = diagnosticReport.performer.first()
        val date = now()
        val serviceRequests = getResources<ServiceRequest>(resources, ResourceType.ResourceTypeId.ServiceRequest).map {
            val observationType = it.code.code()
            resourceService.create(it.apply {
                subject = patientReference
                locationReference = listOf(Reference(locationService.byObservationType(observationType)))
            })
        }
        val carePlan = CarePlan(
                subject = patientReference,
                author = paramedicReference,
                contributor = paramedicReference,
                status = CarePlanStatus.active,
                created = Timestamp(date.time),
                title = "Маршрутный лист",
                activity = serviceRequests
                        .map { CarePlanActivity(outcomeReference = Reference(it)) }
        ).let { resourceService.create(it) }
//        val encounter = Encounter(subject = patientReference).let { resourceService.create(it) }todo encounter это инфа о госпитализации, создадим при заполнении инфы о госпитализации. и в supportingInfo класть?
        val claim = getResources<Claim>(resources, ResourceType.ResourceTypeId.Claim).first().let {
            resourceService.create(it.apply {
                it.patient = patientReference
            })
        }
        val consents = getResources<Consent>(resources, ResourceType.ResourceTypeId.Consent).map {
            resourceService.create(it.apply {
                it.patient = patientReference
                performer = paramedicReference
            })
        }
        val observations = getResources<Observation>(resources, ResourceType.ResourceTypeId.Observation).map {
            resourceService.create(it.apply {
                subject = patientReference
                performer = listOf(paramedicReference)
            })
        }
        val questionnaireResponse = getResources<QuestionnaireResponse>(resources, ResourceType.ResourceTypeId.QuestionnaireResponse).map {
            resourceService.create(it.apply {
                source = patientReference
                author = paramedicReference
            })
        }
        ClinicalImpression(
                status = ClinicalImpressionStatus.active,
                date = date,
                subject = patientReference,
                assessor = paramedicReference,
                summary = "Заключение: ${diagnosticReport.conclusion}",
                supportingInfo = (consents + observations + questionnaireResponse + listOf(claim, diagnosticReport, carePlan)).map { Reference(it) }
        ).let { resourceService.create(it) }
        return patient.id
    }
    override fun activeClinicalImpression(patientId: String): ClinicalImpression? {
        return resourceService.all(ResourceType.ClinicalImpression, RequestBodyForResources(filter = mapOf("status" to ClinicalImpressionStatus.active.toString()))).firstOrNull()
    }

    /**
     * Отменить активное обращение, если таковое имеется.
     * Т к при создании нового может быть по ошибке 2 активных.
     * Поэтому перед созданием нового нужно отменить предыдущее
     */
    private fun cancelActiveClinicalImpression(patientId: String) {
        activeClinicalImpression(patientId)?.let {
            resourceService.update(it.apply {
                status = ClinicalImpressionStatus.canceled
            })
        }
    }


    private fun <T> getResources(resources: List<BaseResource>, type: ResourceType.ResourceTypeId): List<T> where T : BaseResource {
        return resources.filter { it.resourceType == type }.map { it as T }
    }
}