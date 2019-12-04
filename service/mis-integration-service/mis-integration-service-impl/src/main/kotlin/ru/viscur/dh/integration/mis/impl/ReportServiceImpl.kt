package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.dto.CarePlanToPrintDto
import ru.viscur.dh.fhir.model.dto.CarePlanToPrintLocationDto
import ru.viscur.dh.fhir.model.dto.ObservationDuration
import ru.viscur.dh.fhir.model.dto.QueueStatusDuration
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import ru.viscur.dh.fhir.model.valueSets.LocationType
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.integration.mis.api.dto.*
import java.util.*

/**
 * Created at 12.11.2019 9:45 by SherbakovaMA
 */
@Service
class ReportServiceImpl(
        private val patientService: PatientService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val serviceRequestService: ServiceRequestService,
        private val conceptService: ConceptService,
        private val locationService: LocationService,
        private val practitionerService: PractitionerService,
        private val queueService: QueueService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val observationService: ObservationService,
        private val codeMapService: CodeMapService
) : ReportService {

    override fun queueHistoryOfPatient(patientId: String): List<QueueStatusDuration> {
        //за последние сутки
        val periodEnd = now()
        val periodStart = periodEnd.plusDays(-1)
        return queueService.queueHistoryOfPatient(patientId, periodStart, periodEnd).map {
            QueueStatusDuration(it.subject.id!!, it.fireDate, it.status.name, it.location?.id, it.duration)
        }
    }

    override fun observationHistoryOfPatient(patientId: String): List<ObservationDuration> =
            observationDurationService.recentObservationsByPatientId(patientId)

    override fun queueInOffices(): List<QueueInOfficeDto> = queueService.queueItems().groupBy { it.location.id!! }.map {
        val officeId = it.key
        val items = it.value
        queueInOfficeDto(officeId = officeId, items = items)
    }

    override fun queueInOffice(officeId: String): QueueInOfficeDto = queueInOfficeDto(officeId = officeId, items = queueService.queueItemsOfOffice(officeId))

    override fun queueOfPractitioner(practitionerId: String): QueueInOfficeDto? {
        val practitioner = practitionerService.byId(practitionerId)
        if (practitioner.isInspectionQualification()) {
            val observationType = codeMapService.respQualificationToObservationTypes(practitioner.qualificationCategory())
            val queueItems = queueService.queueItemsOfOffices(listOf(
                    RED_ZONE,
                    YELLOW_ZONE_SECTION_1,
                    YELLOW_ZONE_SECTION_2,
                    YELLOW_ZONE_SECTION_3,
                    YELLOW_ZONE_SECTION_4,
                    YELLOW_ZONE_SECTION_5,
                    YELLOW_ZONE_SECTION_6,
                    GREEN_ZONE
            )).filter { queueItem ->
                val patientId = queueItem.subject.id!!
                val activeServiceRequests = serviceRequestService.active(patientId)
                activeServiceRequests.any { it.code.code() == observationType && !it.isInspectionOfResp() }
                        || activeServiceRequests.all { it.code.code() == observationType && it.isInspectionOfResp() && practitionerId in (it.performer!!.map { it.id }) }
            }
            return queueInOfficeDto(items = queueItems, practitionerInOffice = practitioner)
        } else {
            return practitioner.extension.onWorkInOfficeId?.let {
                queueInOffice(it).apply {
                    this.practitioner = PractitionerDto(
                            practitionerId = practitioner.id,
                            name = practitioner.name.first().text
                    )
                }
            }
        }
    }

    override fun workload(): List<QueueInOfficeDto> {
        return practitionerService.all(onWorkOnly = true).mapNotNull {
            queueOfPractitioner(it.id)
        }.filter { it.items.isNotEmpty() }
    }

    override fun workloadHistory(start: Date, end: Date): List<WorkloadItemDto> {
        val observations = observationService.byPeriod(start, end)
        //берем обследования за период. разбиваем по исполнителям (где 2 исполнителя, там дублируется на 2х)
        //группируем по исполнителю, суммируем нагрузку
        return observations.map { observation ->
            val patientId = observation.subject.id!!
            val clinicalImpression = clinicalImpressionService.byServiceRequest(observation.basedOn!!.id!!)
            val workload = clinicalImpression.extension.severity.workloadWeight
            val observationInfo = ObservationInfo(patientId = patientId, workload = workload)
            observation.performer.map { it.id!! to observationInfo }
        }.flatten().groupBy { it.first }.map {
            val practitionerId = it.key
            val observationsOfPractitioner = it.value.map { it.second }
            val practitioner = practitionerService.byId(practitionerId)
            WorkloadItemDto(
                    practitioner = PractitionerDto(
                            practitionerId = practitionerId,
                            name = practitioner.name.first().text
                    ),
                    observationListSize = observationsOfPractitioner.size,
                    workload = observationsOfPractitioner.sumBy { it.workload }
            )
        }
    }

    override fun queueHistory(start: Date, end: Date): List<QueueInOfficeHistoryDto> {
        val queueItems = queueService.queueHistoryByPeriod(start, end)
        return queueItems.groupBy { it.location!!.id!! }.map {
            val officeId = it.key
            val items = it.value
            QueueInOfficeHistoryDto(
                    officeId = officeId,
                    queueItemsSize = items.size,
                    workload = items.map { it.severity.workloadWeight }.sum()
            )
        }
    }

    override fun carePlanToPrint(patientId: String): CarePlanToPrintDto {
        val patient = patientService.byId(patientId)
        val clinicalImpression = clinicalImpressionService.active(patientId)
        val mainSyndrome = patientService.mainSyndrome(patientId)
        val practitionerId = mainSyndrome.performer.first().id!!
        val transportationType = clinicalImpressionService.transportationType(clinicalImpression)
        val transportationTypeConcept = conceptService.byCode(ValueSetName.TRANSPORTATION_TYPES, transportationType)
        val entryType = clinicalImpressionService.entryType(clinicalImpression)
        val entryTypeConcept = conceptService.byCode(ValueSetName.ENTRY_TYPES, entryType)
        return CarePlanToPrintDto(
                clinicalImpressionCode = clinicalImpression.identifierValueNullable(IdentifierType.CARE_PLAN_CODE),
                queueCode = clinicalImpression.extension.queueCode,
                severity = clinicalImpression.extension.severity.display,
                name = patient.name.first().text,
                birthDate = patient.birthDate.toStringWithoutTime(),
                age = patient.age,
                entryType = entryTypeConcept.display,
                mainSyndrome = mainSyndrome.conclusionCode.first().code(),
                practitionerName = practitionerService.byId(practitionerId).name.first().text,
                transportation = transportationTypeConcept.display,
                locations = serviceRequestService.active(patientId).asSequence().filterNot { it.locationReference.isNullOrEmpty() }
                        //не можем использовать нумерацию назначений для нумерации кабинетов, т к множество назначений в один кабинет
                        //конвертим в кабинетId + любой номер назначения в этот кабинет - этого достаточно для определния порядка прохождения кабинетов
                        .groupBy { it.locationReference!!.first().id }.map { Pair(it.key, it.value.first().extension!!.executionOrder) }
                        .sortedBy { it.second }
                        .mapIndexed { index, locationIdWithOrder ->
                            val locationId = locationIdWithOrder.first!!
                            val location = locationService.byId(locationId)
                            //для кабинетов указываем номер кабинета, для зон их тип: Зеленая зона/Желтая зона/Красная зона
                            val locationStr = if (locationService.isZone(locationId)) {
                                conceptService.byCode(ValueSetName.LOCATION_TYPE, location.type()).display
                            } else {
                                location.identifierValue(IdentifierType.OFFICE_NUMBER)
                            }
                            CarePlanToPrintLocationDto(
                                    onum = index + 1,
                                    location = locationStr,
                                    address = location.address!!.text
                            )
                        }.toList()
        )
    }

    private data class ObservationInfo(
            val patientId: String,
            val workload: Int
    )

    private fun queueInOfficeDto(officeId: String? = null, practitionerInOffice: Practitioner? = null, items: List<QueueItem>) = QueueInOfficeDto(
            officeId = officeId,
            queueSize = items.size,
            queueWaitingSum = items.sumBy { it.estDuration },
            queueWorkload = items.sumBy { it.severity!!.workloadWeight },
            items = items.mapIndexed { index, queueItem ->
                val patientId = queueItem.subject.id!!
                val patient = patientService.byId(patientId)
                QueueItemDto(
                        onum = queueItem.onum!! + 1,
                        severity = queueItem.severity!!.name,
                        severityDisplay = queueItem.severity!!.display,
                        name = patient.name.first().text,
                        age = patient.age,
                        estDuration = queueItem.estDuration,
                        queueCode = patientService.queueCode(patientId),
                        patientId = patientId,
                        status = patient.extension.queueStatus!!.name
                )
            }
    ).apply {
        practitionerInOffice?.run {
            practitioner = PractitionerDto(
                    practitionerId = practitionerInOffice.id,
                    name = practitionerInOffice.name.first().text
            )
        }
    }
}