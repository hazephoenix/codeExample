package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.QueueService
import ru.viscur.dh.fhir.model.dto.LocationMonitorDto
import ru.viscur.dh.fhir.model.dto.LocationMonitorNextOfficeForPatientInfoDto
import ru.viscur.dh.fhir.model.dto.LocationMonitorQueueItemDto
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.queue.api.LocationMonitorInformService
import ru.viscur.dh.queue.api.QueueStateDispatcher

/**
 * Created at 19.11.2019 11:36 by SherbakovaMA
 */
@Service
class LocationMonitorInformServiceImpl(
        private val locationService: LocationService,
        private val queueService: QueueService,
        private val queueStateDispatcher: QueueStateDispatcher
) : LocationMonitorInformService {

    override fun queueChanged(officeIds: List<String>) {
        officeIds.forEach { officeId ->
            queueStateDispatcher.add(locationMonitor(officeId))
        }
    }

    override fun queueCurrentState(officeId: String) = locationMonitor(officeId)

    /**
     * Информация для монитора для отображения очереди/приема в кабинет/зоне
     */
    private fun locationMonitor(officeId: String): LocationMonitorDto {
        val office = locationService.byId(officeId)
        return LocationMonitorDto(
                locationMonitorId = officeId,
                officeId = officeId,
                officeStatus = office.status.name,
                locationType = office.type(),
                fireDate = now(),
                items = queueService.queueItemsOfOffice(officeId).map { queueItem ->
                    LocationMonitorQueueItemDto(
                            onum = queueItem.onum!!,
                            patientId = queueItem.subject.id!!,
                            status = queueItem.patientQueueStatus!!.name,
                            severity = queueItem.severity!!.name,
                            queueNumber = queueItem.queueNumber
                    )
                },
                nextOfficeForPatientsInfo = office.extension.nextOfficeForPatientsInfo.map { nextOfficeForPatientInfo ->
                    LocationMonitorNextOfficeForPatientInfoDto(
                            patientId = nextOfficeForPatientInfo.subject.id!!,
                            severity = nextOfficeForPatientInfo.severity.name,
                            queueNumber = nextOfficeForPatientInfo.queueNumber,
                            nextOfficeId = nextOfficeForPatientInfo.nextOffice.id!!
                    )
                }
        )
    }
}