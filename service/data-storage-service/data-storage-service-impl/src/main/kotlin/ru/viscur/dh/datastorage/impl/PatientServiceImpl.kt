package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.CarePlanStatus
import ru.viscur.dh.fhir.model.enums.ClinicalImpressionStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
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
        private val codeMapService: CodeMapService,
        private val conceptService: ConceptService,
        private val practitionerService: PractitionerService
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

    override fun activeServiceRequests(patientId: String, officeId: String): List<ServiceRequest> {
        //active clinicalImpression -> carePlan -> active serviceRequests in office
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
              and sr.resource ->> 'status' = 'active'
              and :officeRef in (
                select jsonb_array_elements(sIntr.resource -> 'locationReference') ->> 'reference'
                from serviceRequest sIntr
                where sIntr.id = sr.id)
            order by sr.resource -> 'extension' ->> 'executionOrder'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("officeRef", "Location/$officeId")
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

    override fun predictServiceRequests(diagnosis: String, gender: String, complaints: List<String>): Bundle {
        //определяем отв. специальности -> врачей данной специальностей -> самый "свободный" на рабочем месте будет отв.
        //услуги в маршрутном листе определяются по диагнозу + осмотр отв.
        val responsibleQualifications = responsibleQualifications(diagnosis, gender, complaints)
        val practitionersId = practitionerService.byQualifications(responsibleQualifications)
        if (practitionersId.isEmpty()) {
            throw Exception("ERROR. Can't find practitioners by qualifications: ${responsibleQualifications.joinToString()}")
        }
        val responsiblePractitionerId = practitionersId.first()//todo смотреть кто в системе + кто менее нагружен пациентами под отв-ю
        val observationTypeOfResponsible = observationTypeOfResponsiblePractitioner(responsiblePractitionerId)

        val diagnosisConcept = conceptService.byCode(ValueSetName.ICD_10.id, diagnosis)
        val observationTypes =
                (codeMapService.icdToObservationTypes(diagnosisConcept.parentCode!!) +
                        observationTypeOfResponsible).distinct()

        val serviceRequests = observationTypes.map { observationType ->
            ServiceRequest(code = observationType)
        }
        serviceRequests.find { it.code.code() == observationTypeOfResponsible }!!.apply { performer = listOf(referenceToPractitioner(responsiblePractitionerId)) }

        return Bundle(
                entry =
                (serviceRequests +
                        ListResource(
                                title = "Предлагаемый список ответсвенных врачей",
                                entry = practitionersId.map { practitionerId -> ListResourceEntry(referenceToPractitioner(practitionerId)) }
                        )
                        )
                        .map { BundleEntry(it) }
        )
    }

    /**
     * По диагнозу + полу пациента + жалобам определяются специальности, которые м б назначены отв. такому пациенту
     */
    private fun responsibleQualifications(diagnosis: String, gender: String, complaints: List<String>): List<String> {
        val diagnosisConcept = conceptService.byCode(ValueSetName.ICD_10.id, diagnosis)
        var qualifications = codeMapService.icdToPractitionerQualifications(diagnosisConcept.parentCode!!)
        //фильтруем по связанному полу. Если были уролог или гинеколог, то один ненужный отсеивается по полу пациента
        qualifications = qualifications.filter {
            val qualification = conceptService.byCode(ValueSetName.PRACTITIONER_QUALIFICATIONS.id, it.code)
            val relativeGender = qualification.relativeGender
            relativeGender.isNullOrEmpty() || relativeGender == gender
        }
        val complaintCodes = conceptService.byAlternative(ValueSetName.COMPLAINTS.id, complaints)
        //случай, если у пациента есть жалобы, указанные в условиях назначения отв. специалиста (например, с сильной болью направляем к хирургу при I70-I79)
        val qualificationsFilteredByComplaints = qualifications.filter { qualification ->
            complaintCodes.any { complaintCode ->
                !qualification.condition.isNullOrEmpty() && complaintCode in qualification.condition!!.map { it.code }
            }
        }
        if (qualificationsFilteredByComplaints.isNotEmpty()) {
            return qualificationsFilteredByComplaints.map { it.code }
        }
        //случай, если у пациента нет сильной боли при I70-I79 (а хирург при сильной боли), тогда к терапевту, т к у него нет условий
        //или случай, если у специальности(ей) нет условий приема для этого диагноза
        val qualificationsWithoutConditions = qualifications.filter { it.condition.isNullOrEmpty() }
        if (qualificationsWithoutConditions.isNotEmpty()) {
            return qualificationsWithoutConditions.map { it.code }
        }
        //случай, если у всех специальностей для диагноза есть условия, которых нет у пациента
        // (A00-A09 - хирург при острой боли, терапевт при лихорадке, а у пациента нет таких жалоб - тогда все равнозначны и без фильтрации
        return qualifications.map { it.code }
    }

    @Tx
    override fun saveFinalPatientData(bundle: Bundle): String {
        val resources = bundle.entry.map { it.resource.apply { id = genId() } }
        var patient = getResources<Patient>(resources, ResourceType.ResourceTypeId.Patient).first()
        val patientEnp = patient.identifier?.find { it.type.code() == IdentifierType.ENP.toString() }?.value
        val patientByEnp = patientEnp?.let { byEnp(patientEnp) }
        //нашли по ЕНП - обновляем, нет - создаем
        patient = patientByEnp?.let { byEnp ->
            resourceService.update(patient.apply {
                id = byEnp.id
                extension.queueStatusUpdatedAt = byEnp.extension.queueStatusUpdatedAt
                extension.queueStatus = byEnp.extension.queueStatus
            })
        } ?: let {
            resourceService.create(patient.apply {
                extension.queueStatusUpdatedAt = now()
                extension.queueStatus = PatientQueueStatus.READY
            })
        }

        cancelActiveClinicalImpression(patient.id)//todo может лучше сообщать о том, что есть активное обращение?

        val patientReference = Reference(patient)
        val diagnosticReport = getResources<DiagnosticReport>(resources, ResourceType.ResourceTypeId.DiagnosticReport)
                .first().let { resourceService.create(it) }
        val paramedicReference = diagnosticReport.performer.first()
        val date = now()

        val serviceRequests = getResources<ServiceRequest>(resources, ResourceType.ResourceTypeId.ServiceRequest)

        // Проверяем наличие направления к ответственному врачу, если нет - создаем
        val responsiblePractitionerRef = getResources<ListResource>(resources, ResourceType.ResourceTypeId.ListResource)
                .firstOrNull()
                ?.entry?.find { it.item.type == ResourceType.ResourceTypeId.Practitioner }?.item
                ?: throw Error("No responsible practitioner provided")
        val responsiblePractitionerId = responsiblePractitionerRef.id!!
        val observationTypeOfResponsible = observationTypeOfResponsiblePractitioner(responsiblePractitionerId)
        val serviceRequestOfResponsiblePr = (serviceRequests.find { it.code.code() == observationTypeOfResponsible }
                ?: ServiceRequest(code = observationTypeOfResponsible))
                .apply { performer = listOf(referenceToPractitioner(responsiblePractitionerId)) }

        var resultServices = serviceRequests.filterNot { it.code.code() == observationTypeOfResponsible } + serviceRequestOfResponsiblePr
        resultServices = resultServices.map {
            val observationType = it.code.code()
            resourceService.create(it.apply {
                subject = patientReference
            })
        }
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
                assessor = responsiblePractitionerRef, // ответственный врач
                summary = "Заключение: ${diagnosticReport.conclusion}",
                supportingInfo = (consents + observations + questionnaireResponse + listOf(claim, diagnosticReport, carePlan)).map { Reference(it) }
        ).let { resourceService.create(it) }
        return patient.id
    }

    /**
     * Тип обследования у отв врача по его id
     */
    private fun observationTypeOfResponsiblePractitioner(responsiblePractitionerId: String): String {
        val responsiblePractitioner = practitionerService.byId(responsiblePractitionerId)
        return codeMapService.respQualificationToObservationTypes(responsiblePractitioner.qualification.code.code())
    }

    override fun activeClinicalImpression(patientId: String): ClinicalImpression? {
        val query = em.createNativeQuery("""
                select ci.resource
                from clinicalImpression ci
                where ci.resource -> 'subject' ->> 'reference' = :patientRef
                  and ci.resource ->> 'status' = 'active'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResource()
    }

    /**
     * Отменить активное обращение, если таковое имеется.
     * Т к при создании нового может быть по ошибке 2 активных.
     * Поэтому перед созданием нового нужно отменить предыдущее
     */
    private fun cancelActiveClinicalImpression(patientId: String) {
        activeClinicalImpression(patientId)?.let {
            resourceService.update(it.apply {
                status = ClinicalImpressionStatus.cancelled
            })
        }
    }

    private fun <T> getResources(resources: List<BaseResource>, type: ResourceType.ResourceTypeId): List<T> where T : BaseResource {
        return resources.filter { it.resourceType == type }.map { it as T }
    }
}