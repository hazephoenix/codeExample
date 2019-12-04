package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.INSPECTION_TYPES
import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.isInspectionOfResp
import ru.viscur.dh.integration.practitioner.app.api.PractitionerAppEventPublisher
import ru.viscur.dh.integration.practitioner.app.api.model.QueuePatientAppDto
import ru.viscur.dh.queue.api.QueueForPractitionersInformService

/**
 * Created at 03.12.2019 14:31 by SherbakovaMA
 */
@Service
class QueueForPractitionersInformServiceImpl(
        private val practitionerService: PractitionerService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val serviceRequestService: ServiceRequestService,
        private val locationService: LocationService,
        private val patientService: PatientService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val practitionerAppEventPublisher: PractitionerAppEventPublisher,
        private val codeMapService: CodeMapService
) : QueueForPractitionersInformService {

    override fun patientAddedToOfficeQueue(patientId: String, officeId: String, onum: Int, estDuration: Int) {
        val targetPractitionersIds = practitionerService.all(onWorkInOfficeId = officeId).map { it.id }.toSet()
        //врачи указывают кабинеты не зоны
        if (targetPractitionersIds.isNotEmpty()) {
            informPractitionersAboutPatientAdded(targetPractitionersIds, patientId, estDuration, onum)
        }
        //это зона
        if (locationService.isZone(officeId)) {
            val serviceRequests = serviceRequestService.active(patientId)
            //есть назначения не по отв.
            //по отв. информируем при мониторинге проведения любого обсл-я
            if (serviceRequests.any { !it.isInspectionOfResp() }) {
                val serviceRequestsNotResp = serviceRequests.filterNot { it.isInspectionOfResp() }
                patientIsReadyForObservations(patientId, serviceRequestsNotResp.map { it.code.code() })
            }
        }
    }

    override fun patientDeletedFromOfficeQueue(patientId: String, officeId: String) {
        //все привязанные к кабинету. в зонах мониторится по проведению обследований
        practitionerAppEventPublisher.publishQueuePatientRemoved(
                targetPractitionersIds = practitionerService.all(onWorkInOfficeId = officeId).map { it.id }.toSet(),
                patientId = patientId
        )
    }

    override fun patientAddedToPractitionerQueue(patientId: String, practitionerId: String, observationType: String) {
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        diagnosis?.run {
            val estDuration = observationDurationService.estimate(observationType, diagnosis, patientService.severity(patientId))
            informPractitionersAboutPatientAdded(setOf(practitionerId), patientId, estDuration)
        }
    }

    override fun patientDeletedFromPractitionerQueue(patientId: String, practitionerId: String) {
        practitionerAppEventPublisher.publishQueuePatientRemoved(setOf(practitionerId), patientId)
    }

    override fun patientIsReadyForObservations(patientId: String, observationTypes: List<String>) {
        val diagnosis = patientService.preliminaryDiagnosticConclusion(patientId)
        diagnosis?.run {
            observationTypes.forEach { observationType ->
                //если это назначение относится к осмотрам, то есть соотв-е к категории специальностей
                inspectionQualificationCategories(listOf(observationType))?.run {
                    val estDuration = observationDurationService.estimate(observationType, diagnosis, patientService.severity(patientId))
                    val targetPractitionersIds = practitionerService.byQualificationCategories(this).map { it.id }.toSet()
                    informPractitionersAboutPatientAdded(targetPractitionersIds, patientId, estDuration)
                }
            }
        }
    }

    override fun patientDontNeedInspectionAnymore(patientId: String, observationTypes: List<String>) {
        inspectionQualificationCategories(observationTypes)?.run {
            val targetPractitionersIds = practitionerService.byQualificationCategories(this).map { it.id }.toSet()
            practitionerAppEventPublisher.publishQueuePatientRemoved(targetPractitionersIds, patientId)
        }
    }

    override fun resultsAreReadyInCarePlan(patientId: String, clinicalImpression: ClinicalImpression) {
        clinicalImpression.assessor?.run {
            practitionerAppEventPublisher.publishObservationsResultsAreReady(setOf(this.id!!), clinicalImpression)
        }
    }

    private fun informPractitionersAboutPatientAdded(targetPractitionersIds: Set<String>, patientId: String, estDuration: Int, onum: Int? = null) {
        val clinicalImpression = clinicalImpressionService.active(patientId)
        practitionerAppEventPublisher.publishNewQueuePatient(
                targetPractitionersIds,
                clinicalImpression,
                QueuePatientAppDto(
                        id = patientId,
                        orderInQueue = onum,
                        severity = clinicalImpression.extension.severity,
                        code = clinicalImpression.extension.queueCode,
                        timeToProvideService = estDuration
                )
        )
    }

    /**
     * По типам услуг определяем категории тех, кто проводит осмотр
     * Если нет осмотров в услугах (не [INSPECTION_TYPES]), то возвращает null
     */
    private fun inspectionQualificationCategories(observationTypes: List<String>): List<String>? {
        val inspectionTypes = observationTypes.filter { it in INSPECTION_TYPES }
        val qualifications = codeMapService.allRespQualificationToObservationTypes().filter { it.targetCode.any { it.code in inspectionTypes } }.map { it.sourceCode }
        return if (qualifications.isEmpty()) null else qualifications
    }
}