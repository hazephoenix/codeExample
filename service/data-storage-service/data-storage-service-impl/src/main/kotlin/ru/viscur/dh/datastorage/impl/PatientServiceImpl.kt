package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.enums.Severity
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

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
}