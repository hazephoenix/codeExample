package ru.viscur.dh.integration.doctorapp.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.DoctorCallService
import ru.viscur.dh.datastorage.api.DoctorMessageService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.criteria.CriteriaOrderBy
import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.criteria.DoctorMessageCriteria
import ru.viscur.dh.datastorage.api.model.call.CallStatus
import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization
import ru.viscur.dh.datastorage.api.model.message.DoctorMessage
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.cmd.*
import ru.viscur.dh.integration.doctorapp.api.event.DoctorAppEvent
import ru.viscur.dh.integration.doctorapp.api.event.DoctorCallAcceptedEvent
import ru.viscur.dh.integration.doctorapp.api.event.DoctorCallCreatedEvent
import ru.viscur.dh.integration.doctorapp.api.event.DoctorCallDeclinedEvent
import ru.viscur.dh.integration.doctorapp.api.model.*
import ru.viscur.dh.integration.doctorapp.api.request.DoctorCallsRequest
import ru.viscur.dh.integration.doctorapp.api.response.DoctorCallsResponse
import ru.viscur.dh.integration.doctorapp.impl.mapper.DoctorAppMapper
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.security.ForbiddenException
import ru.viscur.dh.security.currentUserDetails
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import java.util.*

@Service
class DoctorAppServiceImpl(
        val locationService: LocationService,
        val practitionerService: PractitionerService,
        val doctorCallService: DoctorCallService,
        val reportService: ReportService,
        val doctorMessageService: DoctorMessageService,
        val doctorAppMapper: DoctorAppMapper,
        val eventPublisher: ApplicationEventPublisher
) : DoctorAppService {
    override fun newCall(cmd: NewDoctorCallCmd): DoctorCall {
        val user = currentUserDetails()
        var storageValue = ru.viscur.dh.datastorage.api.model.call.DoctorCall(
                id = "",
                dateTime = Date(),
                caller = practitionerService.byId(user.id),
                specialization = cmd.specialization,
                doctor = practitionerService.byId(cmd.doctorId),
                goal = cmd.goal,
                patientSeverity = cmd.patientSeverity,
                location = locationService.byId(cmd.locationId),
                comment = cmd.comment,
                status = CallStatus.Awaiting,
                timeToArrival = null
        )
        storageValue = doctorCallService.createDoctorCall(storageValue)
        val apiValue = doctorAppMapper.mapCallStorageToApi(storageValue)

        eventPublisher.publishEvent(DoctorAppEvent(
                content = DoctorCallCreatedEvent(apiValue),
                targetUsersIds = setOf(
                        apiValue.doctor.id
                )
        ))
        return apiValue
    }

    override fun acceptCall(cmd: AcceptDoctorCallCmd): DoctorCall {
        val call = shouldBeDoctorOf(doctorCallService.byId(cmd.callId))
        call.status = CallStatus.Accepted
        call.timeToArrival = cmd.timeToArrival
        val updatedCall = doctorCallService.updateDoctorCall(call)

        eventPublisher.publishEvent(DoctorAppEvent(
                content = DoctorCallAcceptedEvent(updatedCall.id, updatedCall.timeToArrival ?: 15),
                targetUsersIds = setOf(
                        updatedCall.doctor.id,
                        updatedCall.caller.id
                )
        ))
        return doctorAppMapper.mapCallStorageToApi(updatedCall)

    }


    override fun declineCall(cmd: DeclineDoctorCallCmd): DoctorCall {
        val call = shouldBeDoctorOf(doctorCallService.byId(cmd.callId))
        call.status = CallStatus.Declined

        val updatedCall = doctorCallService.updateDoctorCall(call)

        eventPublisher.publishEvent(DoctorAppEvent(
                content = DoctorCallDeclinedEvent(updatedCall.id),
                targetUsersIds = setOf(
                        updatedCall.doctor.id,
                        updatedCall.caller.id
                )
        ))
        return doctorAppMapper.mapCallStorageToApi(updatedCall)
    }

    override fun findCallableDoctors(): List<CallableDoctor> {
        return practitionerService.byQualifications(
                CallableSpecialization
                        .values()
                        .map { it.name }
        ).map(doctorAppMapper::mapPractitionerToCallableDoctor)
    }

    override fun findLocations(): List<Location> {
        return locationService.byLocationType("Inspection" /* TODO Constants*/)
                .map(doctorAppMapper::mapLocationFhirToApi)
    }


    override fun findIncomingCalls(request: PagedRequest): PagedResponse<DoctorCall> {
        val user = currentUserDetails()
        return doctorCallService.findCalls(
                request.withCriteria(
                        DoctorCallCriteria(
                                doctorIdIn = setOf(user.id),
                                orderBy = listOf(CriteriaOrderBy.desc("dateTime"))
                        ))
        ).map(doctorAppMapper::mapCallStorageToApi)

    }

    override fun findOutcomingCall(request: PagedRequest): PagedResponse<DoctorCall> {
        val user = currentUserDetails()
        return doctorCallService.findCalls(
                request.withCriteria(DoctorCallCriteria(
                        callerIdIn = setOf(user.id),
                        orderBy = listOf(CriteriaOrderBy.desc("dateTime"))
                ))
        ).map(doctorAppMapper::mapCallStorageToApi)
    }

    override fun getQueuePatients(): List<QueuePatient> {
        val user = currentUserDetails()
        return reportService
                .queueOfPractitioner(user.id)
                ?.items
                ?.map { doctorAppMapper.mapQueueOfficeToQueuePatient(it) }
                ?: listOf()

    }

    override fun findMessages(request: PagedRequest, actual: Boolean): PagedResponse<Message> {
        val user = currentUserDetails()
        return doctorMessageService.findMessages(request.withCriteria(DoctorMessageCriteria(
                listOf(user.id),
                if (actual) DoctorMessageCriteria.Type.Actual else DoctorMessageCriteria.Type.Hidden,
                listOf(CriteriaOrderBy.desc("dateTime"))
        ))).map {
            doctorAppMapper.mapMessage(it)
        }
    }

    override fun hideMessage(messageId: String): Message {
        var entity = doctorMessageService.byId(messageId)
        val user = currentUserDetails()
        if (entity.doctor.id != user.id) {
            throw ForbiddenException()
        }
        entity.hidden = true
        entity = doctorMessageService.updateMessage(entity)
        return doctorAppMapper.mapMessage(entity)
    }

    private fun shouldBeDoctorOf(it: ru.viscur.dh.datastorage.api.model.call.DoctorCall): ru.viscur.dh.datastorage.api.model.call.DoctorCall {
        val user = currentUserDetails()
        if (user.id == it.doctor.id) {
            return it;
        }
        throw ForbiddenException()
    }
}