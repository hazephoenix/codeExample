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
        private val claimService: ClaimService,
        private val observationService: ObservationService
) : ClinicalImpressionService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun active(patientId: String): ClinicalImpression? {
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
        active(patientId)?.run {
            resourceService.update(this.apply {
                status = ClinicalImpressionStatus.cancelled
            })
            val references = this.supportingInfo
            getResources(references, ResourceType.CarePlan).firstOrNull()?.run {
                if (this.status in listOf(CarePlanStatus.active, CarePlanStatus.waiting_results, CarePlanStatus.results_are_ready)) {
                    resourceService.update(this.apply {
                        status = CarePlanStatus.cancelled
                    })
                    getResources(this.activity.map { it.outcomeReference }, ResourceType.ServiceRequest).forEach { serviceRequest ->
                        if (serviceRequest.status in listOf(ServiceRequestStatus.active, ServiceRequestStatus.waiting_result)) {
                            resourceService.update(serviceRequest.apply {
                                status = ServiceRequestStatus.cancelled
                            })
                            observationService.byBaseOnServiceRequestId(serviceRequest.id)?.run {
                                if (this.status == ObservationStatus.registered) {
                                    resourceService.update(this.apply {
                                        status = ObservationStatus.cancelled
                                    })
                                }
                            }
                        }
                    }
                }
            }
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
    override fun finish(bundle: Bundle): ClinicalImpression {
        val resources = bundle.entry.map { it.resource }
        val diagnosticReport = getResourcesFromList<DiagnosticReport>(resources, ResourceType.DiagnosticReport.id).first()
        val patientId = diagnosticReport.subject.id ?: throw Error("No patient id provided in DiagnosticReport.subject")

        active(patientId)?.let { clinicalImpression ->
            resourceService.create(diagnosticReport)
            var refs = listOf(Reference(diagnosticReport))
            getResourcesFromList<Encounter>(resources, ResourceType.Encounter.id)
                    .firstOrNull()
                    ?.let { encounter ->
                        resourceService.create(encounter)
                        refs = refs.plus(Reference(encounter))
                    }

            carePlanService.current(patientId)?.let {
                resourceService.update(it.apply {
                    status = CarePlanStatus.completed
                })
            }

            claimService.active(patientId)?.let {
                resourceService.update(it.apply {
                    status = ClaimStatus.completed
                })
            }

            clinicalImpression.status = ClinicalImpressionStatus.completed
            clinicalImpression.supportingInfo = refs.plus(clinicalImpression.supportingInfo)

            resourceService.update(clinicalImpression)
            return clinicalImpression
        } ?: throw Error("No active ClinicalImpression for patient with id $patientId found")
    }

    private fun <T> getResources(references: List<Reference>, resourceType: ResourceType<T>): List<T>
            where T : BaseResource =
            references.filter { it.type == resourceType.id }.map { resourceService.byId(resourceType, it.id!!) }

}