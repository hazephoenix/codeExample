package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
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

    override fun byId(id: String): Patient? = resourceService.byId(ResourceType.Patient, id)


    override fun severity(patientId: String): Severity {
        val q = em.createNativeQuery("""
            select jsonb_array_elements(items.item -> 'answer') -> 'valueCoding' ->> 'code' as severity
            from (
                     select jsonb_array_elements(r.resource -> 'item') as item
                     from questionnaireresponse r
                     where r.resource ->> 'questionnaire' = 'Questionnaire/Severity_criteria'
                       and 'QuestionnaireResponse/' || r.id in (
                         select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference' as qrRef
                         from clinicalImpression ci
                         where ci.resource -> 'subject' ->> 'reference' = :patientRef
                     )
                 ) as items
            where items.item ->> 'linkId' = 'Severity'
        """.trimIndent())
        q.setParameter("patientRef", "Patient/$patientId")
        val severityStr = (q.singleResult as Array<Any>)[0] as String
        return enumValueOf(severityStr)
    }

    /**
     * Метод сохраняет конечную (заполненную полностью) информацию о пациенте
     * по окончании приема фельдшера (/reception/patient)
     */
    fun saveFinalPatientData(bundle: Bundle): Bundle {
        val resources = bundle.entry.map { it.resource }
        val date = Date()
        val diagnosticReport = getResources(resources, ResourceType.ResourceTypeId.DiagnosticReport).first() as DiagnosticReport
        val paramedicReference = diagnosticReport.performer.first()
        val patient = getResources(resources, ResourceType.ResourceTypeId.Patient).first() as Patient
        val carePlan = CarePlan(
                subject = Reference(patient),
                author = paramedicReference,
                contributor = paramedicReference,
                status = CarePlanStatus.active,
                created = Timestamp(date.time),
                title = "Маршрутный лист",
                actitity = getResources(resources, ResourceType.ResourceTypeId.ServiceRequest)
                        .map { CarePlanActivity(outcomeReference = Reference(it)) }
        )
        val encounter = Encounter(subject = Reference(patient))
        val clinicalImpression = ClinicalImpression(
                status = ClinicalImpressionStatus.completed,
                date = date,
                subject = Reference(patient),
                assessor = paramedicReference,
                summary = "Заключение: ${diagnosticReport.conclusion}",
                encounter = Reference(encounter)
        )

        resources.forEach { resourceService.create(it) }
        resourceService.create(carePlan)
        resourceService.create(encounter)
        resourceService.create(clinicalImpression)

        val services = getResources(resources, ResourceType.ResourceTypeId.ServiceRequest)
        return Bundle(entry = services.map { BundleEntry(it) }
                .sortedBy { (it.resource as ServiceRequest).extension.executionOrder })
    }

    private fun <T> getResources(resources: List<BaseResource>, type: T): List<BaseResource> {
        return resources.filter { it.resourceType == type }
    }
}