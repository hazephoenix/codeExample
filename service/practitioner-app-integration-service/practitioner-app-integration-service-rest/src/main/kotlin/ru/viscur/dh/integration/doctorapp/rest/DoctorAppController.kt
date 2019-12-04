package ru.viscur.dh.integration.doctorapp.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.cmd.*
import ru.viscur.dh.integration.doctorapp.api.model.PractitionerAppDto
import ru.viscur.dh.integration.doctorapp.api.model.DoctorCallAppDto
import ru.viscur.dh.integration.doctorapp.api.model.LocationAppDto
import ru.viscur.dh.integration.doctorapp.api.model.MessageAppDto

@RestController
@RequestMapping("/integration/doctor-app")
class DoctorAppController(
        private val doctorAppService: DoctorAppService
) {

    @PostMapping("/new-call")
    @PreAuthorize("hasAnyRole('MIS_PRACTITIONER')")
    fun newCall(@RequestBody cmd: CreatePractitionerCallAppCmd): DoctorCallAppDto =
            doctorAppService.newCall(cmd)

    @PostMapping("/accept-call")
    fun acceptCall(@RequestBody cmd: AcceptPractitionerCallAppCmd): DoctorCallAppDto =
            doctorAppService.acceptCall(cmd)

    @PostMapping("/decline-call")
    fun declineCall(@RequestBody cmd: DeclinePractitionerCallAppCmd): DoctorCallAppDto =
            doctorAppService.declineCall(cmd)

    @GetMapping("/callable-doctors")
    fun findAllCallableDoctors(): List<PractitionerAppDto> =
            doctorAppService.findCallableDoctors()

    @GetMapping("/locations")
    fun findLocations(): List<LocationAppDto> =
            doctorAppService.findLocations()

    @PutMapping("/incoming-calls")
    fun findIncomingCalls(@RequestBody request: PagedRequest) =
            doctorAppService.findIncomingCalls(request)

    @PutMapping("/outcoming-calls")
    fun findOutcomingCalls(@RequestBody request: PagedRequest) =
            doctorAppService.findOutcomingCall(request)

    @GetMapping("/queue-patients")
    fun getQueuePatients() =
            doctorAppService.getQueuePatients()

    @PutMapping(path = ["/messages"], params = ["actual"])
    fun findActualMessages(
            @RequestBody request: PagedRequest,
            @RequestParam("actual") actual: Boolean
    ): PagedResponse<MessageAppDto> {
        return doctorAppService.findMessages(request, actual)
    }

    @PutMapping(path = ["/messages/hide/{messageId}"])
    fun hideMessage(@PathVariable("messageId") messageId: String): MessageAppDto {
        return doctorAppService.hideMessage(messageId)
    }

}