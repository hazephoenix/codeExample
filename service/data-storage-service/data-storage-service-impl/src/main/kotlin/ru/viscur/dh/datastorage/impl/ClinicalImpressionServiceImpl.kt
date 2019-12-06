package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.QUESTIONNAIRE_ID_COMMON_INFO
import ru.viscur.dh.datastorage.api.util.QUESTIONNAIRE_LINK_ID_ENTRY_TYPE
import ru.viscur.dh.datastorage.api.util.QUESTIONNAIRE_LINK_ID_TRANSPORTATION_TYPE
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.referenceToPatient
import ru.viscur.dh.fhir.model.utils.resources
import ru.viscur.dh.transaction.desc.config.annotation.Tx
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

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun allActive(): List<ClinicalImpression> =
            resourceService.all(ResourceType.ClinicalImpression, RequestBodyForResources(filter = mapOf("status" to ClinicalImpressionStatus.active.name)))

    override fun hasActive(patientId: String): ClinicalImpression? {
        val query = em.createNativeQuery("""
                select ci.resource
                from clinicalImpression ci
                where ci.resource -> 'subject' ->> 'reference' = :patientRef
                  and ci.resource ->> 'status' = '${ClinicalImpressionStatus.active.name}'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResource()
    }

    override fun active(patientId: String): ClinicalImpression =
            hasActive(patientId) ?: throw Error("No active ClinicalImpression for patient with id '$patientId' found")

    override fun byServiceRequest(serviceRequestId: String): ClinicalImpression {
        val query = em.createNativeQuery("""
                select cir
                from (select ci.resource cir, jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference' ciSiRef
                      from clinicalimpression ci
                     ) ciInfo
                         join
                     (select jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference' srRef, cp.id cpId
                      from CarePlan cp) srInfo
                     on ciInfo.ciSiRef = 'CarePlan/' || srInfo.cpId
                where  srInfo.srRef = 'ServiceRequest/' || :servReqId
        """)
        query.setParameter("servReqId", serviceRequestId)
        return query.fetchResource()
                ?: throw Exception("Not found ClinicalImpression by serviceRequest with id: '$serviceRequestId'")
    }

    override fun cancelActive(patientId: String) {
        hasActive(patientId)?.run {
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
                            observationService.cancelByBaseOnServiceRequestId(serviceRequest.id)
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
    override fun completeRelated(patientId: String, bundle: Bundle): ClinicalImpression {
        return active(patientId).let { clinicalImpression ->
            resourceService.update(ResourceType.ClinicalImpression, clinicalImpression.id) {
                val refToPatient = referenceToPatient(patientId)
                val diagnosticReport = bundle.resources(ResourceType.DiagnosticReport).first()
                val createdDiagnosticReport = resourceService.create(diagnosticReport.apply { subject = refToPatient })
                val refs = mutableListOf(Reference(createdDiagnosticReport))
                bundle.resources(ResourceType.Encounter)
                        .singleOrNull()
                        ?.let { encounter ->
                            val createdEncounter = resourceService.create(encounter.apply { subject = refToPatient })
                            refs += Reference(createdEncounter)
                        } ?: throw Exception("Error. Not found single encounter in bundle: '${bundle.toJsonb()}'")

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
        }
    }

    @Tx
    override fun complete(clinicalImpression: ClinicalImpression): ClinicalImpression {
        val completedClinicalImpression = resourceService.update(ResourceType.ClinicalImpression, clinicalImpression.id) {
            status = ClinicalImpressionStatus.completed
        }
        getResources(completedClinicalImpression.supportingInfo, ResourceType.CarePlan).firstOrNull()?.run {
            carePlanService.complete(this.id)
        }
        return completedClinicalImpression
    }

    override fun entryType(clinicalImpression: ClinicalImpression) =
            responseCode(clinicalImpression, QUESTIONNAIRE_ID_COMMON_INFO, QUESTIONNAIRE_LINK_ID_ENTRY_TYPE)

    override fun transportationType(clinicalImpression: ClinicalImpression) =
            responseCode(clinicalImpression, QUESTIONNAIRE_ID_COMMON_INFO, QUESTIONNAIRE_LINK_ID_TRANSPORTATION_TYPE)

    /**
     * Код ответа на вопрос с [linkId] в опроснике [questionnaireId]
     */
    private fun responseCode(clinicalImpression: ClinicalImpression, questionnaireId: String, linkId: String) =
            getResources(clinicalImpression.supportingInfo, ResourceType.QuestionnaireResponse)
                    .map { resourceService.byId(ResourceType.QuestionnaireResponse, it.id) }
                    .find { it.questionnaire == "Questionnaire/$questionnaireId" }
                    ?.item?.find { it.linkId == linkId }?.answer?.first()?.valueCoding?.code
                    ?: throw Exception("not found answer for questionnaire $questionnaireId and linkId $linkId for" +
                            " patient with id '${clinicalImpression.subject.id()}' (clinicalImpressionId: '${clinicalImpression.id}')")

    private fun <T> getResources(references: List<Reference>, resourceType: ResourceType<T>): List<T>
            where T : BaseResource =
            references.filter { it.type == resourceType.id }.map { resourceService.byId(resourceType, it.id()) }
}