package ru.viscur.dh.integration.doctorapp.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.DoctorMessageService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PractitionerCallStorageService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.criteria.CriteriaOrderBy
import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.criteria.DoctorMessageCriteria
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.cmd.*
import ru.viscur.dh.integration.doctorapp.api.model.*
import ru.viscur.dh.integration.doctorapp.impl.mapper.DoctorAppMapper
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.practitioner.call.api.PractitionerCallService
import ru.viscur.dh.practitioner.call.api.cmd.AcceptPractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.cmd.CommandInitiator
import ru.viscur.dh.practitioner.call.api.cmd.CreatePractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.cmd.DeclinePractitionerCallCmd
import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory
import ru.viscur.dh.practitioner.call.model.PractitionerCall
import ru.viscur.dh.security.ForbiddenException
import ru.viscur.dh.security.currentUserDetails
import ru.viscur.dh.transaction.desc.config.annotation.Tx

@Service
class DoctorAppServiceImpl(
        val locationService: LocationService,
        val practitionerService: PractitionerService,
        val practitionerCallService: PractitionerCallService,
        val practitionerCallStorageService: PractitionerCallStorageService,
        val reportService: ReportService,
        val doctorMessageService: DoctorMessageService,
        val doctorAppMapper: DoctorAppMapper,
        val eventPublisher: ApplicationEventPublisher
) : DoctorAppService {

    @Tx
    override fun newCall(cmd: CreatePractitionerCallAppCmd): DoctorCallAppDto {
        val user = currentUserDetails()
        val call = practitionerCallService.createCall(
                CreatePractitionerCallCmd(
                        user.id,
                        cmd.practitionerId,
                        cmd.specializationCategory,
                        cmd.goal,
                        cmd.locationId,
                        cmd.patientSeverity,
                        cmd.comment
                )
        )
        return doctorAppMapper.mapPractitionerCallToApp(call)
    }

    @Tx
    override fun acceptCall(cmd: AcceptPractitionerCallAppCmd): DoctorCallAppDto {
        currentUserMustBePractitionerIn(practitionerCallService.byId(cmd.callId))
        val call = practitionerCallService.acceptCall(
                AcceptPractitionerCallCmd(cmd.callId, cmd.timeToArrival)
        )
        return doctorAppMapper.mapPractitionerCallToApp(call)
    }


    @Tx
    override fun declineCall(cmd: DeclinePractitionerCallAppCmd): DoctorCallAppDto {
        currentUserMustBePractitionerIn(practitionerCallService.byId(cmd.callId))
        val call = practitionerCallService.declineCall(
                DeclinePractitionerCallCmd(
                        cmd.callId,
                        CommandInitiator.User
                )
        )
        return doctorAppMapper.mapPractitionerCallToApp(call)
    }


    override fun findCallableDoctors(): List<PractitionerAppDto> {
        return practitionerService.byQualificationCategories(
                CallableSpecializationCategory
                        .values()
                        .map { it.fhirId }
        ).map(doctorAppMapper::mapPractitionerToCallableDoctor)
    }

    override fun findLocations(): List<LocationAppDto> {
        return locationService.byLocationType("Inspection" /* TODO Constants*/)
                .map(doctorAppMapper::mapLocationToApp)
    }


    override fun findIncomingCalls(request: PagedRequest): PagedResponse<DoctorCallAppDto> {
        val user = currentUserDetails()
        return practitionerCallStorageService.findCalls(
                request.withCriteria(
                        DoctorCallCriteria(
                                doctorIdIn = setOf(user.id),
                                orderBy = listOf(CriteriaOrderBy.desc("dateTime"))
                        ))
        ).map(doctorAppMapper::mapPractitionerCallToApp)

    }

    override fun findOutcomingCall(request: PagedRequest): PagedResponse<DoctorCallAppDto> {
        val user = currentUserDetails()
        return practitionerCallStorageService.findCalls(
                request.withCriteria(DoctorCallCriteria(
                        callerIdIn = setOf(user.id),
                        orderBy = listOf(CriteriaOrderBy.desc("dateTime"))
                ))
        ).map(doctorAppMapper::mapPractitionerCallToApp)
    }

    override fun getQueuePatients(): List<QueuePatientAppDto> {
        val user = currentUserDetails()
        return reportService
                .queueOfPractitioner(user.id)
                ?.items
                ?.map { doctorAppMapper.mapQueueOfficeToQueuePatient(it) }
                ?: listOf()

    }

    override fun findMessages(request: PagedRequest, actual: Boolean): PagedResponse<MessageAppDto> {
        val user = currentUserDetails()
        return doctorMessageService.findMessages(request.withCriteria(DoctorMessageCriteria(
                listOf(user.id),
                if (actual) DoctorMessageCriteria.Type.Actual else DoctorMessageCriteria.Type.Hidden,
                listOf(CriteriaOrderBy.desc("dateTime"))
        ))).map {
            doctorAppMapper.mapMessage(it)
        }
    }

    override fun hideMessage(messageId: String): MessageAppDto {
        var entity = doctorMessageService.byId(messageId)
        val user = currentUserDetails()
        if (entity.doctor.id != user.id) {
            throw ForbiddenException()
        }
        entity.hidden = true
        entity = doctorMessageService.updateMessage(entity)
        return doctorAppMapper.mapMessage(entity)
    }

    private fun currentUserMustBePractitionerIn(call: PractitionerCall): PractitionerCall {
        val user = currentUserDetails()
        if (user.id == call.practitioner.id) {
            return call
        }
        throw ForbiddenException()
    }
}