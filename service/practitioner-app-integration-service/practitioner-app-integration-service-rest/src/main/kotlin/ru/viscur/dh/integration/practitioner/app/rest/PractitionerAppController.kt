package ru.viscur.dh.integration.practitioner.app.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.datastorage.api.request.PagedRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.integration.practitioner.app.api.PractitionerAppService
import ru.viscur.dh.integration.practitioner.app.api.cmd.AcceptPractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.cmd.CreatePractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.cmd.DeclinePractitionerCallAppCmd
import ru.viscur.dh.integration.practitioner.app.api.model.PractitionerAppDto
import ru.viscur.dh.integration.practitioner.app.api.model.PractitionerCallAppDto
import ru.viscur.dh.integration.practitioner.app.api.model.LocationAppDto
import ru.viscur.dh.integration.practitioner.app.api.model.MessageAppDto

@RestController
@RequestMapping("/integration/practitioner-app")
class PractitionerAppController(
        private val practitionerAppService: PractitionerAppService
) {

    @PostMapping("/new-call")
    fun newCall(@RequestBody cmd: CreatePractitionerCallAppCmd): PractitionerCallAppDto =
            practitionerAppService.newCall(cmd)

    @PostMapping("/accept-call")
    fun acceptCall(@RequestBody cmd: AcceptPractitionerCallAppCmd): PractitionerCallAppDto =
            practitionerAppService.acceptCall(cmd)

    @PostMapping("/decline-call")
    fun declineCall(@RequestBody cmd: DeclinePractitionerCallAppCmd): PractitionerCallAppDto =
            practitionerAppService.declineCall(cmd)

    @GetMapping("/callable-practitioners")
    fun findAllCallablePractitioners(): List<PractitionerAppDto> =
            practitionerAppService.findCallablePractitioners()

    @GetMapping("/locations")
    fun findLocations(): List<LocationAppDto> =
            practitionerAppService.findLocations()

    @PutMapping("/incoming-calls")
    fun findIncomingCalls(@RequestBody request: PagedRequest) =
            practitionerAppService.findIncomingCalls(request)

    @PutMapping("/outcoming-calls")
    fun findOutcomingCalls(@RequestBody request: PagedRequest) =
            practitionerAppService.findOutcomingCall(request)

    @GetMapping("/queue-patients")
    fun getQueuePatients() =
            practitionerAppService.getQueuePatients()

    @PutMapping(path = ["/messages"], params = ["actual"])
    fun findActualMessages(
            @RequestBody request: PagedRequest,
            @RequestParam("actual") actual: Boolean
    ): PagedResponse<MessageAppDto> {
        return practitionerAppService.findMessages(request, actual)
    }

    @PutMapping(path = ["/messages/hide/{messageId}"])
    fun hideMessage(@PathVariable("messageId") messageId: String): MessageAppDto {
        return practitionerAppService.hideMessage(messageId)
    }

}