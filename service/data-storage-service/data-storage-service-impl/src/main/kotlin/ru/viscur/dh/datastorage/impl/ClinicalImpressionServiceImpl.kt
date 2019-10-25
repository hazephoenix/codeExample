package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.annotation.*
import ru.viscur.dh.datastorage.impl.utils.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import javax.persistence.*

@Service
class ClinicalImpressionServiceImpl(
        private val resourceService: ResourceService,
        private val carePlanService: CarePlanService,
        private val claimService: ClaimService
) : ClinicalImpressionService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getActive(patientId: String): ClinicalImpression? {
        val query = em.createNativeQuery("""
                select ci.resource
                from clinicalImpression ci
                where ci.resource -> 'subject' ->> 'reference' = :patientRef
                  and ci.resource ->> 'status' = 'active'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResource()
    }

    override fun cancelActive(patientId: String) {
        getActive(patientId)?.let {
            resourceService.update(it.apply {
                status = ClinicalImpressionStatus.canceled
            })
        }
    }

    /**
     * Завершить обращение пациента
     *
     * Ответсвенный врач выносит окончательный диагноз [DiagnosticReport] и принимает решение о
     * госпитализации пациента ([Encounter].hospitalization), при этом в диагноз  помимо кода МКБ-10
     * добавляется
     */
    @Tx
    override fun finish(bundle: Bundle) {
        val resources = bundle.entry.map { it.resource }
        val diagnosticReport = getResourcesFromList<DiagnosticReport>(resources, ResourceType.DiagnosticReport.id).first()
        val patientId = diagnosticReport.subject.id ?: throw Error("No patient id provided in DiagnosticReport.subject")

        getActive(patientId)?.let { clinicalImpression ->
            resourceService.create(diagnosticReport)
            var refs = listOf(Reference(diagnosticReport))
            getResourcesFromList<Encounter>(resources, ResourceType.Encounter.id)
                    .firstOrNull()
                    ?.let { encounter ->
                        resourceService.create(encounter)
                        refs = refs.plus(Reference(encounter))
                    }

            carePlanService.getActive(patientId)?.let {
                resourceService.update(it.apply {
                    status = CarePlanStatus.completed
                })
            }

            claimService.getActive(patientId)?.let {
                resourceService.update(it.apply {
                    status = ClaimStatus.completed
                })
            }

            clinicalImpression.status = ClinicalImpressionStatus.completed
            clinicalImpression.supportingInfo = refs.plus(clinicalImpression.supportingInfo.orEmpty())

            resourceService.update(clinicalImpression)
        } ?: throw Error("No active ClinicalImpression for patient with id $patientId found")
    }
}