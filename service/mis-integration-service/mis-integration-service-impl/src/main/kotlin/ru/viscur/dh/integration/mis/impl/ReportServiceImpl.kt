package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.OFFICE_101
import ru.viscur.dh.fhir.model.dto.ObservationDuration
import ru.viscur.dh.fhir.model.dto.QueueStatusDuration
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.utils.*
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
        private val practitionerService: PractitionerService,
        private val queueService: QueueService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val observationService: ObservationService
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

    override fun queueInOffices(withPractitioners: Boolean): List<QueueInOfficeDto> = queueService.queueItems().groupBy { it.location.id!! }.map {
        val officeId = it.key
        val items = it.value
        queueInOfficeDto(officeId, items, withPractitioners)
    }.flatten()

    override fun queueInOffice(officeId: String): List<QueueInOfficeDto> = queueInOfficeDto(officeId, queueService.queueItemsOfOffice(officeId))

    override fun queueOfPractitioner(practitionerId: String): List<QueueInOfficeDto> {
        //todo здесь нужно определение по локации в каком кабинете находится врач
        val officeId = OFFICE_101
        return queueInOffice(officeId)
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

    private data class ObservationInfo(
            val patientId: String,
            val workload: Int
    )

    private fun queueInOfficeDto(officeId: String, items: List<QueueItem>, withPractitioners: Boolean = false): List<QueueInOfficeDto> {
        val queueInOfficeDto = QueueInOfficeDto(
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
                            age = patient.age
                    )
                }
        )
        if (withPractitioners) {
            //todo здесь нужно определение по локации какие врачи находятся в кабинете officeId
            val practitionerIdsInOffice = when (officeId) {
                "Office:101" -> listOf("фельдшер_Колосова", "мед_работник_диагностики_Иванова")
                "Office:140" -> listOf("мед_работник_диагностики_Сидорова")
                "Office:150" -> listOf("терапевт_Петров")
                "Office:151" -> listOf("терапевт_Иванов")
                "Office:116" -> listOf("хирург_Петров")
                "Office:117" -> listOf("хирург_Иванов")
                "Office:104" -> listOf("невролог_Петров")
                "Office:139" -> listOf("невролог_Иванов")
                "Office:149" -> listOf("уролог_Петров")
                "Office:129" -> listOf("уролог_Иванов")
                "Office:130" -> listOf("гинеколог_Петров")
                "Office:202" -> listOf("гинеколог_Иванов")
                else -> listOf()
            }
            return practitionerIdsInOffice.map {
                val practitionerInOffice = practitionerService.byId(it)
                queueInOfficeDto.apply {
                    practitioner = PractitionerDto(
                            practitionerId = practitionerInOffice.id,
                            name = practitionerInOffice.name.first().text
                    )
                }
            }
        }
        return listOf(queueInOfficeDto)
    }
}