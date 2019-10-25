package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.datastorage.impl.utils.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.fhir.model.valueSets.*
import java.sql.Timestamp
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 15.10.2019 11:52 by SherbakovaMA
 */
@Service
class PatientServiceImpl(
        private val resourceService: ResourceService,
        private val locationService: LocationService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val serviceRequestService: ServiceRequestService
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
        var patient = getResourcesFromList<Patient>(resources, ResourceType.ResourceTypeId.Patient).first()
        val patientEnp = patient.identifier?.find { it.type.code() == IdentifierType.ENP.toString() }?.value
        val patientByEnp = patientEnp?.let { byEnp(patientEnp) }
        //нашли по ЕНП - обновляем, нет - создаем
        patient = patientByEnp?.let { byEnp -> resourceService.update(patient.apply { id = byEnp.id }) }
                ?: let { resourceService.create(patient) }

        clinicalImpressionService.cancelActive(patient.id)//todo может лучше сообщать о том, что есть активное обращение?

        val patientReference = Reference(patient)
        val diagnosticReport = getResourcesFromList<DiagnosticReport>(resources, ResourceType.ResourceTypeId.DiagnosticReport)
                .first().let { resourceService.create(it) }
        val paramedicReference = diagnosticReport.performer.first()
        val date = now()

        val serviceRequests = getResourcesFromList<ServiceRequest>(resources, ResourceType.ResourceTypeId.ServiceRequest).map {
            val observationType = it.code.code()
            resourceService.create(it.apply {
                subject = patientReference
                locationReference = listOf(Reference(locationService.byObservationType(observationType)))
            })
        }

        // Проверяем наличие направления к ответственному врачу, если нет - создаем
        val responsiblePractitionerRef = getResourcesFromList<ListResource>(resources, ResourceType.ResourceTypeId.ListResource)
                .firstOrNull()
                ?.entry?.find { it.item.type == ResourceType.ResourceTypeId.Practitioner }?.item
                ?: throw Error("No responsible practitioner provided")

        val extraServices =
                if (serviceRequests.any { it.performer?.first() == responsiblePractitionerRef }) {
                    listOf(serviceRequestService.createForPractitioner(responsiblePractitionerRef))
                } else listOf()
        val resultServices = serviceRequests + extraServices

        val carePlan = CarePlan(
                subject = patientReference,
                author = responsiblePractitionerRef, // ответственный врач
                contributor = paramedicReference,
                status = CarePlanStatus.active,
                created = Timestamp(date.time),
                title = "Маршрутный лист",
                activity = resultServices
                        .map { CarePlanActivity(outcomeReference = Reference(it)) }
        ).let { resourceService.create(it) }
//        val encounter = Encounter(subject = patientReference).let { resourceService.create(it) }todo encounter это инфа о госпитализации, создадим при заполнении инфы о госпитализации. и в supportingInfo класть?
        val claim = getResourcesFromList<Claim>(resources, ResourceType.ResourceTypeId.Claim).first().let {
            resourceService.create(it.apply {
                it.patient = patientReference
            })
        }
        val consents = getResourcesFromList<Consent>(resources, ResourceType.ResourceTypeId.Consent).map {
            resourceService.create(it.apply {
                it.patient = patientReference
                performer = paramedicReference
            })
        }
        val observations = getResourcesFromList<Observation>(resources, ResourceType.ResourceTypeId.Observation).map {
            resourceService.create(it.apply {
                subject = patientReference
                performer = listOf(paramedicReference)
            })
        }
        val questionnaireResponse = getResourcesFromList<QuestionnaireResponse>(resources, ResourceType.ResourceTypeId.QuestionnaireResponse).map {
            resourceService.create(it.apply {
                source = patientReference
                author = paramedicReference
            })
        }
        ClinicalImpression(
                status = ClinicalImpressionStatus.active,
                date = date,
                subject = patientReference,
                assessor = responsiblePractitionerRef, // ответственный врач
                summary = "Заключение: ${diagnosticReport.conclusion}",
                supportingInfo = (consents + observations + questionnaireResponse + listOf(claim, diagnosticReport, carePlan)).map { Reference(it) }
        ).let { resourceService.create(it) }
        return patient.id
    }
}