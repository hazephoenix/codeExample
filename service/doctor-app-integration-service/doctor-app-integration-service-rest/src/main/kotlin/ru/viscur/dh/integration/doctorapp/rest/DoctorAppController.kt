package ru.viscur.dh.integration.doctorapp.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.cmd.AcceptDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.DeclineDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.NewDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.model.CallableDoctor
import ru.viscur.dh.integration.doctorapp.api.model.DoctorCall
import ru.viscur.dh.integration.doctorapp.api.model.Location

@RestController
@RequestMapping("/integration/doctor-app")
class DoctorAppController(
        private val doctorAppService: DoctorAppService
) {

    @PostMapping("/new-call")
    @PreAuthorize("hasAnyRole('MIS_PRACTITIONER')")
    fun newCall(@RequestBody cmd: NewDoctorCallCmd): DoctorCall =
            doctorAppService.newCall(cmd)

    @PostMapping("/accept-call")
    fun acceptCall(@RequestBody cmd: AcceptDoctorCallCmd): DoctorCall =
            doctorAppService.acceptCall(cmd)

    @PostMapping("/decline-call")
    fun declineCall(@RequestBody cmd: DeclineDoctorCallCmd): DoctorCall =
            doctorAppService.declineCall(cmd)

    @GetMapping("/callable-doctors")
    fun findAllCallableDoctors(): List<CallableDoctor> =
            doctorAppService.findCallableDoctors()

    @GetMapping("/locations")
    fun findLocations(): List<Location> =
            doctorAppService.findLocations()

    @PutMapping("/incoming-calls")
    fun findIncomingCalls(@RequestBody request: PagedRequest) =
            doctorAppService.findIncomingCalls(request)

    @PutMapping("/outcoming-calls")
    fun findOutcomingCalls(@RequestBody request: PagedRequest) =
            doctorAppService.findOutcomingCall(request)
}