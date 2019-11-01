package ru.viscur.dh.datastorage.impl

import jdk.management.resource.ResourceId
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.datastorage.impl.utils.getResourcesFromList
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.Reference
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
class ClinicalImpressionServiceImpl(
        private val resourceService: ResourceService,
        private val carePlanService: CarePlanService,
        private val claimService: ClaimService,
        private val observationService: ObservationService,
        private val serviceRequestService: ServiceRequestService
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

    override fun bySupportingInfoReference(refResourceType: ResourceType.ResourceTypeId, refResourceId: String): ClinicalImpression {
        val query = em.createNativeQuery("""
                select r.resource
                from ClinicalImpression r
                where :refResourceType || '/' || :refResourceId in (
                    select
                        jsonb_array_elements(rIntr.resource -> 'supportingInfo') ->> 'reference'
                    from ClinicalImpression rIntr
                    where rIntr.id = r.id
                )
        """)
        query.setParameter("refResourceType", refResourceType.toString())
        query.setParameter("refResourceId", refResourceId)
        return query.fetchResource()
                ?: throw Exception("Not found ClinicalImpression with reference '$refResourceType/refResourceId' in supportingInfo")
    }

    override fun byServiceRequest(serviceRequestId: String): ClinicalImpression {
        val query = em.createNativeQuery("""
                select cir
                from (select ci.resource cir, jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference' ciSiRef
                      from clinicalimpression ci
                      where ci.resource ->> 'status' = 'active'
                     ) ciInfo
                         join
                     (select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference' srRef, cp.id cpId
                      from CarePlan cp) srInfo
                     on ciInfo.ciSiRef = 'CarePlan/' || srInfo.cpId
                where  srInfo.srRef = 'ServiceRequest/' || :servReqId
        """)
        query.setParameter("servReqId", serviceRequestId)
        return query.fetchResource()
                ?: throw Exception("Not found active ClinicalImpression by serviceRequest with id: '$serviceRequestId'")
    }

    override fun cancelActive(patientId: String) {
        active(patientId)?.run {
            resourceService.update(ResourceType.ClinicalImpression, id) {
                status = ClinicalImpressionStatus.cancelled
            }
            val references = this.supportingInfo
            getResources(references, ResourceType.CarePlan).firstOrNull()?.run {
                if (this.status in listOf(CarePlanStatus.active, CarePlanStatus.waiting_results, CarePlanStatus.results_are_ready)) {
                    resourceService.update(ResourceType.CarePlan, id) {
                        status = CarePlanStatus.cancelled
                    }
                    getResources(this.activity.map { it.outcomeReference }, ResourceType.ServiceRequest).forEach { serviceRequest ->
                        if (serviceRequest.status in listOf(ServiceRequestStatus.active, ServiceRequestStatus.waiting_result)) {
                            resourceService.update(ResourceType.ServiceRequest, serviceRequest.id) {
                                status = ServiceRequestStatus.cancelled
                            }
                            observationService.byBaseOnServiceRequestId(serviceRequest.id)?.run {
                                if (status == ObservationStatus.registered) {
                                    resourceService.update(ResourceType.Observation, id) {
                                        status = ObservationStatus.cancelled
                                    }
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
    override fun completeRelated(bundle: Bundle): ClinicalImpression {
        val resources = bundle.entry.map { it.resource }
        val diagnosticReport = getResourcesFromList<DiagnosticReport>(resources, ResourceType.DiagnosticReport.id).first()
        val patientId = diagnosticReport.subject.id ?: throw Error("No patient id provided in DiagnosticReport.subject")

        return active(patientId)?.let { clinicalImpression ->
            resourceService.update(ResourceType.ClinicalImpression, clinicalImpression.id) {
                val createdDiagnosticReport = resourceService.create(diagnosticReport)
                val refs = mutableListOf(Reference(createdDiagnosticReport))
                getResourcesFromList<Encounter>(resources, ResourceType.Encounter.id)
                        .singleOrNull()
                        ?.let { encounter ->
                            val createdEncounter = resourceService.create(encounter)
                            refs += Reference(createdEncounter)
                        } ?: throw Exception("Error. Not found single encounter in bundle: '${bundle.toJsonb()}'")
                getResourcesFromList<Observation>(resources, ResourceType.Observation.id)
                        .singleOrNull()
                        ?.let { observation ->
                            serviceRequestService.updateStatusByObservation(observation)
                            observationService.create(patientId, observation)
                        } ?: throw Exception("Error. Not found single observation in bundle: '${bundle.toJsonb()}'")

                carePlanService.current(patientId)?.let {
                    resourceService.update(ResourceType.CarePlan, it.id) {
                        status = CarePlanStatus.completed
                    }
                } ?: throw Exception("Error. Not found current CarePlan for patient with id: '$patientId'")

                claimService.active(patientId)?.let {
                    resourceService.update(ResourceType.Claim, it.id) {
                        status = ClaimStatus.completed
                    }
                }

                supportingInfo += refs
            }
        } ?: throw Error("Error. No active ClinicalImpression for patient with id $patientId found")
    }

    @Tx
    override fun complete(clinicalImpression: ClinicalImpression): ClinicalImpression =
            resourceService.update(ResourceType.ClinicalImpression, clinicalImpression.id) {
                status = ClinicalImpressionStatus.completed
            }

    private fun <T> getResources(references: List<Reference>, resourceType: ResourceType<T>): List<T>
            where T : BaseResource =
            references.filter { it.type == resourceType.id }.map { resourceService.byId(resourceType, it.id!!) }

}