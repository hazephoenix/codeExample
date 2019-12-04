package ru.viscur.dh.integration.practitioner.app.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PractitionerMessageService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PractitionerCallStorageService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.criteria.CriteriaOrderBy
import ru.viscur.dh.datastorage.api.criteria.PractitionerCallCriteria
import ru.viscur.dh.datastorage.api.criteria.PractitionerMessageCriteria
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.practitioner.app.api.PractitionerAppService
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.integration.practitioner.app.api.cmd.AcceptPractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.cmd.CreatePractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.cmd.DeclinePractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.model.*
import ru.viscur.dh.integration.practitioner.app.impl.mapper.PractitionerAppMapper
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
class PractitionerAppServiceImpl(
        val locationService: LocationService,
        val practitionerService: PractitionerService,
        val practitionerCallService: PractitionerCallService,
        val practitionerCallStorageService: PractitionerCallStorageService,
        val reportService: ReportService,
        val practitionerMessageService: PractitionerMessageService,
        val practitionerAppMapper: PractitionerAppMapper,
        val eventPublisher: ApplicationEventPublisher
) : PractitionerAppService {

    @Tx
    override fun newCall(cmd: CreatePractitionerCallAppCmd): PractitionerCallAppDto {
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
        return practitionerAppMapper.mapPractitionerCallToApp(call)
    }

    @Tx
    override fun acceptCall(cmd: AcceptPractitionerCallAppCmd): PractitionerCallAppDto {
        currentUserMustBePractitionerIn(practitionerCallService.byId(cmd.callId))
        val call = practitionerCallService.acceptCall(
                AcceptPractitionerCallCmd(cmd.callId, cmd.timeToArrival)
        )
        return practitionerAppMapper.mapPractitionerCallToApp(call)
    }


    @Tx
    override fun declineCall(cmd: DeclinePractitionerCallAppCmd): PractitionerCallAppDto {
        currentUserMustBePractitionerIn(practitionerCallService.byId(cmd.callId))
        val call = practitionerCallService.declineCall(
                DeclinePractitionerCallCmd(
                        cmd.callId,
                        CommandInitiator.User
                )
        )
        return practitionerAppMapper.mapPractitionerCallToApp(call)
    }


    override fun findCallablePractitioners(): List<PractitionerAppDto> {
        return practitionerService.byQualificationCategories(
                CallableSpecializationCategory
                        .values()
                        .map { it.fhirId }
        ).map(practitionerAppMapper::mapPractitionerToApp)
    }

    override fun findLocations(): List<LocationAppDto> {
        return locationService.byLocationType("Inspection" /* TODO Constants*/)
                .map(practitionerAppMapper::mapLocationToApp)
    }


    override fun findIncomingCalls(request: PagedRequest): PagedResponse<PractitionerCallAppDto> {
        val user = currentUserDetails()
        return practitionerCallStorageService.findCalls(
                request.withCriteria(
                        PractitionerCallCriteria(
                                practitionerIdIn = setOf(user.id),
                                orderBy = listOf(CriteriaOrderBy.desc("dateTime"))
                        ))
        ).map(practitionerAppMapper::mapPractitionerCallToApp)

    }

    override fun findOutcomingCall(request: PagedRequest): PagedResponse<PractitionerCallAppDto> {
        val user = currentUserDetails()
        return practitionerCallStorageService.findCalls(
                request.withCriteria(PractitionerCallCriteria(
                        callerIdIn = setOf(user.id),
                        orderBy = listOf(CriteriaOrderBy.desc("dateTime"))
                ))
        ).map(practitionerAppMapper::mapPractitionerCallToApp)
    }

    override fun getQueuePatients(): List<QueuePatientAppDto> {
        val user = currentUserDetails()
        return reportService
                .queueOfPractitioner(user.id)
                ?.items
                ?.map { practitionerAppMapper.mapQueueOfficeToQueuePatient(it) }
                ?: listOf()

    }

    override fun findMessages(request: PagedRequest, actual: Boolean): PagedResponse<MessageAppDto> {
        val user = currentUserDetails()
        return practitionerMessageService.findMessages(request.withCriteria(PractitionerMessageCriteria(
                listOf(user.id),
                if (actual) PractitionerMessageCriteria.Type.Actual else PractitionerMessageCriteria.Type.Hidden,
                listOf(CriteriaOrderBy.desc("dateTime"))
        ))).map {
            practitionerAppMapper.mapMessage(it)
        }
    }

    override fun hideMessage(messageId: String): MessageAppDto {
        var entity = practitionerMessageService.byId(messageId)
        val user = currentUserDetails()
        if (entity.practitioner.id != user.id) {
            throw ForbiddenException()
        }
        entity.hidden = true
        entity = practitionerMessageService.updateMessage(entity)
        return practitionerAppMapper.mapMessage(entity)
    }

    private fun currentUserMustBePractitionerIn(call: PractitionerCall): PractitionerCall {
        val user = currentUserDetails()
        if (user.id == call.practitioner.id) {
            return call
        }
        throw ForbiddenException()
    }
}