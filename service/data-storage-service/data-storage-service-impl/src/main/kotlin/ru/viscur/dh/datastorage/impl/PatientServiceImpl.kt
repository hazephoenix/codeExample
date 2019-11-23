package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.BANDAGE
import ru.viscur.dh.datastorage.api.util.INSPECTION_ON_RECEPTION
import ru.viscur.dh.datastorage.api.util.QUESTIONNAIRE_LINK_ID_SEVERITY
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.utils.ResponsibleQualificationsPredictor
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.*
import javax.persistence.*

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
        private val practitionerService: PractitionerService,
        private val responsibleQualificationsPredictor: ResponsibleQualificationsPredictor
) : PatientService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun byId(id: String): Patient = resourceService.byId(ResourceType.Patient, id)

    override fun byEnp(value: String): Patient? = resourceService.byIdentifier(ResourceType.Patient, IdentifierType.ENP, value)

    override fun severity(patientId: String): Severity {
        //находим clinicalImpression пациента со статусом active
        //у него указан severity
        val q = em.createNativeQuery("""
            select ci.resource -> 'extension' ->> 'severity'
            from clinicalImpression ci
            where ci.resource -> 'subject' ->> 'reference' = :patientRef
            and ci.resource ->> 'status' = 'active'
        """.trimIndent())
        q.setParameter("patientRef", "Patient/$patientId")
        val severityStr = q.resultList.map { it as String }.firstOrNull()
                ?: throw Exception("not found active clinical impression for patient id '$patientId' (for calculation of severity)")
        return enumValueOf(severityStr)
    }

    override fun queueCode(patientId: String) = clinicalImpressionService.active(patientId).extension.queueCode

    override fun updateSeverity(patientId: String, severity: Severity): Boolean {
        var updated = false
        val activeClinicalImpression = clinicalImpressionService.active(patientId)
        resourceService.update(ResourceType.ClinicalImpression, activeClinicalImpression.id) {
            if (extension.severity.name != severity.name) {
                extension.severity = severity
                updated = true
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
            where r.resource ->> 'status' = '${DiagnosticReportStatus.preliminary}'
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

    override fun finalDiagnosticReport(patientId: String): DiagnosticReport {
        val query = em.createNativeQuery("""
            select r.resource
            from diagnosticReport r
            where r.resource ->> 'status' = '${DiagnosticReportStatus.final}'
              and 'DiagnosticReport/' || r.id in (
                select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                from clinicalImpression ci
                where ci.resource -> 'subject' ->> 'reference' = :patientRef
                  and ci.resource ->> 'status' = 'active'
            )""")
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResource()
                ?: throw Exception("not found final DiagnosticReport for patient with id '$patientId'")
    }

    override fun predictServiceRequests(diagnosis: String, gender: String, complaints: List<String>): Bundle {
        //определяем отв. специальности -> врачей данной специальностей -> самый "свободный" на рабочем месте будет отв.
        //услуги в маршрутном листе определяются по диагнозу + осмотр отв.
        val responsibleQualifications = responsibleQualificationsPredictor.predict(diagnosis, gender, complaints)
        val respPractitioners = practitionerService.byQualifications(responsibleQualifications)
        if (respPractitioners.isEmpty()) {
            throw Exception("ERROR. Can't find practitioners by qualifications: ${responsibleQualifications.joinToString()}")
        }
        //todo смотреть кто на работе по локации
        // practitionersId = practitionersId.filter кто на работе сейчас

        //берем наименее загруженного врача
        val responsiblePractitionerId = respPractitioners.map { practitioner ->
            Pair(patientsToExamine(practitioner.id).sumBy { enumValueOf<Severity>(it.severity).workloadWeight }, practitioner)
        }.sortedWith(compareBy({ it.first }, { it.second.name.first().family })).first().second.id
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
                                entry = respPractitioners.map { practitioner -> ListResourceEntry(referenceToPractitioner(practitioner.id)) }
                        )
                        )
                        .map { BundleEntry(it) }
        )
    }

    @Tx
    override fun saveFinalPatientData(bundle: Bundle): String {
        var patient = bundle.resources(ResourceType.Patient).first()
        val queueCode = patient.identifier?.find { it.type.code() == IdentifierType.QUEUE_CODE.name }?.value
                ?: throw Exception("Not defined QUEUE_CODE identifier")
        patient = creaeteOrUpdatePatient(patient)

        val patientId = patient.id
        clinicalImpressionService.cancelActive(patientId)

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
                supportingInfo = (consents + observations + questionnaireResponse + listOf(claim, diagnosticReport, carePlan)).map { Reference(it) },
                extension = ClinicalImpressionExtension(
                        severity = enumValueOf(severity),
                        queueCode = queueCode
                )
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

    @Tx
    override fun saveFinalPatientDataForBandage(bundle: Bundle): String {
        var patient = bundle.resources(ResourceType.Patient).first()
        val queueCode = patient.identifier?.find { it.type.code() == IdentifierType.QUEUE_CODE.name }?.value
                ?: throw Exception("Not defined QUEUE_CODE identifier")
        patient = creaeteOrUpdatePatient(patient)

        val patientId = patient.id
        clinicalImpressionService.cancelActive(patientId)

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

        val bandageServiceRequest = ServiceRequest(code = BANDAGE).let {
            it.subject = patientReference
            resourceService.create(it)
        }

        val carePlan = CarePlan(
                subject = patientReference,
                contributor = paramedicReference,
                status = CarePlanStatus.active,
                created = now,
                title = "Маршрутный лист",
                activity = listOf(bandageServiceRequest)
                        .map { CarePlanActivity(outcomeReference = Reference(it)) }
        ).let { resourceService.create(it) }
        val claim = bundle.resources(ResourceType.Claim).firstOrNull()?.let {
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

        val inspectionOnReceptionStart = consents.firstOrNull()?.dateTime
        ClinicalImpression(
                status = ClinicalImpressionStatus.active,
                date = inspectionOnReceptionStart ?: now,
                subject = patientReference,
                summary = "Заключение: ${diagnosticReport.conclusion}",
                supportingInfo = (consents + observations + questionnaireResponse + listOf(claim, diagnosticReport, carePlan)).filterNotNull().map { Reference(it) },
                extension = ClinicalImpressionExtension(
                        severity = Severity.GREEN,
                        queueCode = queueCode,
                        forBandageOnly = true
                )
        ).let { resourceService.create(it) }
        inspectionOnReceptionStart?.run {
            observationDurationService.saveToHistory(
                    patientId,
                    INSPECTION_ON_RECEPTION,
                    diagnosticReport.conclusionCode.first().code(),
                    Severity.GREEN,
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

    override fun withLongGoingToObservation(): List<String> {
        val query = em.createNativeQuery("""
            select r.id
            from patient r
            where (r.resource->'extension'->>'queueStatusUpdatedAt')\:\:bigint <= :criticalTime
                and r.resource->'extension'->>'queueStatus' = '${PatientQueueStatus.GOING_TO_OBSERVATION}'
        """)
        query.setParameter("criticalTime", criticalTimeForDeletingNextOfficeForPatientsInfo().time)
        return query.resultList.map { it as String }
    }

    private fun creaeteOrUpdatePatient(patient: Patient): Patient {
        //код очереди не храним в пациенте, перекладываем в обращение
        val identifiersWithoutQueueCode = patient.identifier?.filter { it.type.code() != IdentifierType.QUEUE_CODE.name }
        val patientEnp = patient.identifier?.find { it.type.code() == IdentifierType.ENP.name }?.value
        val patientByEnp = patientEnp?.let { byEnp(patientEnp) }
        //нашли по ЕНП - обновляем, нет - создаем
        return patientByEnp?.let { byEnp ->
            resourceService.update(ResourceType.Patient, byEnp.id) {
                identifier = identifiersWithoutQueueCode
                name = patient.name
                birthDate = patient.birthDate
                gender = patient.gender
                extension.nationality = patient.extension.nationality
                extension.birthPlace = patient.extension.birthPlace
            }
        } ?: let {
            resourceService.create(patient.apply {
                id = genId()
                identifier = identifiersWithoutQueueCode
                extension.queueStatusUpdatedAt = now()
                extension.queueStatus = PatientQueueStatus.READY
            })
        }
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
                            queueOfficeId = it[4] as String?,
                            patient = it[5].toResourceEntity()!!
                    )
                }
                .filterNotNull()
                .toList()
    }
}