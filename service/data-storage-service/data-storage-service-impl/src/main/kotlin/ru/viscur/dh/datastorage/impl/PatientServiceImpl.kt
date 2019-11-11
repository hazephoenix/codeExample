package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.INSPECTION_ON_RECEPTION
import ru.viscur.dh.datastorage.api.util.QUESTIONNAIRE_LINK_ID_SEVERITY
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query

/**
 * Created at 15.10.2019 11:52 by SherbakovaMA
 */
@Service
class PatientServiceImpl(
        private val resourceService: ResourceService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val codeMapService: CodeMapService,
        private val conceptService: ConceptService,
        private val observationDurationService: ObservationDurationEstimationService,
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
        val severityStr = q.resultList.map { it as String }.firstOrNull()
                ?: throw Exception("not found severity for patient id '$patientId'")
        return enumValueOf(severityStr)
    }

    override fun updateSeverity(patientId: String, severity: Severity): Boolean {
        val questionnaireResponse = questionnaireResponseForSeverityCriteria(patientId)
                ?: throw Exception("not found questionnaireResponse for SeverityCriteria of patient with id '$patientId'")
        var updated = false
        resourceService.update(ResourceType.QuestionnaireResponse, questionnaireResponse.id) {
            val linkIdOfSeverity = QUESTIONNAIRE_LINK_ID_SEVERITY
            item = item.map {
                if (it.linkId == linkIdOfSeverity) {
                    if (it.answer.first().valueCoding?.code != severity.name) {
                        it.answer = listOf(QuestionnaireResponseItemAnswer(
                                valueCoding = Coding(code = severity.name, display = severity.translation, system = ValueSetName.SEVERITY.id)
                        ))
                        updated = true
                    }
                }
                it
            }
        }
        return updated
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
        return diagnosticReport?.conclusionCode?.first()?.code()
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
        var patient = bundle.resources(ResourceType.Patient).first()
        val patientEnp = patient.identifier?.find { it.type.code() == IdentifierType.ENP.toString() }?.value
        val patientByEnp = patientEnp?.let { byEnp(patientEnp) }
        //нашли по ЕНП - обновляем, нет - создаем
        patient = patientByEnp?.let { byEnp ->
            resourceService.update(ResourceType.Patient, byEnp.id) {
                identifier = patient.identifier
                name = patient.name
                birthDate = patient.birthDate
                gender = patient.gender
                extension.nationality = patient.extension.nationality
                extension.birthPlace = patient.extension.birthPlace
            }
        } ?: let {
            resourceService.create(patient.apply {
                extension.queueStatusUpdatedAt = now()
                extension.queueStatus = PatientQueueStatus.READY
            })
        }

        val patientId = patient.id
        clinicalImpressionService.cancelActive(patientId)//todo может лучше сообщать о том, что есть активное обращение?

        val patientReference = Reference(patient)
        val diagnosticReport = bundle.resources(ResourceType.DiagnosticReport)
                .firstOrNull()?.let {
                    resourceService.create(it.apply {
                        subject = patientReference
                    })
                }
                ?: throw Error("No DiagnosticReport provided")
        val paramedicReference = diagnosticReport.performer.first()
        val now = now()

        val serviceRequests = bundle.resources(ResourceType.ServiceRequest)

        // Проверяем наличие направления к ответственному врачу, если нет - создаем
        val responsiblePractitionerRef = bundle.resources(ResourceType.ListResource)
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
            resourceService.create(it.apply {
                subject = patientReference
            })
        }

        val carePlan = CarePlan(
                subject = patientReference,
                author = responsiblePractitionerRef, // ответственный врач
                contributor = paramedicReference,
                status = CarePlanStatus.active,
                created = now,
                title = "Маршрутный лист",
                activity = resultServices
                        .map { CarePlanActivity(outcomeReference = Reference(it)) }
        ).let { resourceService.create(it) }
        val claim = bundle.resources(ResourceType.Claim).first().let {
            resourceService.create(it.apply {
                it.patient = patientReference
            })
        }
        val consents = bundle.resources(ResourceType.Consent).map {
            resourceService.create(it.apply {
                it.patient = patientReference
                performer = paramedicReference
            })
        }
        val observations = bundle.resources(ResourceType.Observation).map {
            resourceService.create(it.apply {
                subject = patientReference
                performer = listOf(paramedicReference)
            })
        }
        val questionnaireResponse = bundle.resources(ResourceType.QuestionnaireResponse).map {
            resourceService.create(it.apply {
                source = patientReference
                author = paramedicReference
            })
        }
        val severity = questionnaireResponse.flatMap { it.item }.find {
            it.linkId == QUESTIONNAIRE_LINK_ID_SEVERITY
        }?.let {
            it.answer.first().valueCoding?.code
        } ?: throw Exception("not found linkId 'Severity' in questionnaire response")

        val inspectionOnReceptionStart = consents.firstOrNull()?.dateTime
        ClinicalImpression(
                status = ClinicalImpressionStatus.active,
                date = inspectionOnReceptionStart ?: now,
                subject = patientReference,
                assessor = responsiblePractitionerRef, // ответственный врач
                summary = "Заключение: ${diagnosticReport.conclusion}",
                supportingInfo = (consents + observations + questionnaireResponse + listOf(claim, diagnosticReport, carePlan)).map { Reference(it) }
        ).let { resourceService.create(it) }
        inspectionOnReceptionStart?.run {
            observationDurationService.saveToHistory(
                    patientId,
                    INSPECTION_ON_RECEPTION,
                    diagnosticReport.conclusionCode.first().code(),
                    enumValueOf(severity),
                    inspectionOnReceptionStart,
                    now
            )
        }
        return patientId
    }

    override fun patientsToExamine(practitionerId: String?): List<PatientToExamine> {
        var queryStr = """
            select * from patients_to_examine            
        """
        val params = mutableListOf<String>()
        practitionerId?.run {
            queryStr += "where resp_practitioner_id = ?1"
            params += practitionerId
        }
        val query = em.createNativeQuery(queryStr)
        query.setParameters(params)
        return query.patientsToExamine()
    }

    private fun questionnaireResponseForSeverityCriteria(patientId: String): QuestionnaireResponse? {
        val q = em.createNativeQuery("""
            select r.resource
                     from questionnaireResponse r
                     where r.resource ->> 'questionnaire' = 'Questionnaire/Severity_criteria'
                       and 'QuestionnaireResponse/' || r.id in (
                         select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                         from clinicalImpression ci
                         where ci.resource -> 'subject' ->> 'reference' = :patientRef
                            and ci.resource ->> 'status' = 'active'
                     )
        """.trimIndent())
        q.setParameter("patientRef", "Patient/$patientId")
        return q.fetchResource()
    }

    /**
     * Тип обследования у отв врача по его id
     */
    private fun observationTypeOfResponsiblePractitioner(responsiblePractitionerId: String): String {
        val responsiblePractitioner = practitionerService.byId(responsiblePractitionerId)
        return codeMapService.respQualificationToObservationTypes(responsiblePractitioner.qualification.code.code())
    }

    private fun Query.patientsToExamine(): List<PatientToExamine> {
        return this.resultList
                .asSequence()
                .map {
                    it as Array<*>
                    PatientToExamine(
                            practitionerId = it[0] as String,
                            patientId = it[1] as String,
                            severity = it[2] as String,
                            carePlanStatus = enumValueOf(it[3] as String),
                            patient = it[4].toResourceEntity()!!
                    )
                }
                .filterNotNull()
                .toList()
    }
}