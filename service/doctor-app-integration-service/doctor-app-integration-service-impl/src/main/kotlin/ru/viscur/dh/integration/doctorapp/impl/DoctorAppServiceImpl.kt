package ru.viscur.dh.integration.doctorapp.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.DoctorCallService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.criteria.CriteriaOrderBy
import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.model.call.CallStatus
import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.cmd.*
import ru.viscur.dh.integration.doctorapp.api.model.*
import ru.viscur.dh.integration.doctorapp.api.request.DoctorCallsRequest
import ru.viscur.dh.integration.doctorapp.api.response.DoctorCallsResponse
import ru.viscur.dh.integration.doctorapp.impl.mapper.DoctorAppMapper
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.security.ForbiddenException
import ru.viscur.dh.security.currentUserDetails
import java.util.*

@Service
class DoctorAppServiceImpl(
        val locationService: LocationService,
        val practitionerService: PractitionerService,
        val doctorCallService: DoctorCallService,
        val reportService: ReportService,
        val doctorAppMapper: DoctorAppMapper
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

        /*
           TODO:
               2) Найти WebSocket коннекшен(ы) целевого врача
               3) Отправить вызов
        */
        return doctorAppMapper.mapCallStorageToApi(storageValue)
    }

    override fun acceptCall(cmd: AcceptDoctorCallCmd): DoctorCall {
        val call = shouldBeDoctorOf(doctorCallService.byId(cmd.callId))
        call.status = CallStatus.Accepted
        call.timeToArrival = cmd.timeToArrival
        return doctorAppMapper.mapCallStorageToApi(
                doctorCallService.updateDoctorCall(call)
        )
/*
TODO
                3) Найти WebSocket коннекшен(ы) целевого врача
                4) Отправить обновление
        */
    }


    override fun declineCall(cmd: DeclineDoctorCallCmd): DoctorCall {
        val call = shouldBeDoctorOf(doctorCallService.byId(cmd.callId))
        call.status = CallStatus.Declined
        return doctorAppMapper.mapCallStorageToApi(
                doctorCallService.updateDoctorCall(call)
        )
        /*
           TODO:
               3) Найти WebSocket коннекшен(ы) целевого врача
               4) Отправить обновление
       */
    }

    override fun findCallableDoctors(): List<CallableDoctor> {
        return practitionerService.byQualificationsInst(
                CallableSpecialization
                        .values()
                        .map { it.name }
        ).map(doctorAppMapper::mapPractitionerToCallableDoctor)
    }

    override fun findLocations(): List<Location> {
        return locationService.byLocationType("Inspection" /* TODO Constants*/)
                .map(doctorAppMapper::mapLocationFhirToApi)
    }


    override fun callableDoctorStatusChanged(doctor: CallableDoctorStatusChangedCmd) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                .firstOrNull()?.run {
                    items.map { doctorAppMapper.mapQueueOfficeToQueuePatient(it) }
                } ?: listOf()
    }

    private fun shouldBeDoctorOf(it: ru.viscur.dh.datastorage.api.model.call.DoctorCall): ru.viscur.dh.datastorage.api.model.call.DoctorCall {
        val user = currentUserDetails()
        if (user.id == it.doctor.id) {
            return it;
        }
        throw ForbiddenException()
    }
}