package ru.viscur.dh.location.impl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.location.api.service.PractitionersPositioningService
import ru.viscur.dh.location.impl.service.Positions

@RestController
@RequestMapping("/locations")
class LocationsController(
    private val positions: Positions,
    private val practitionersPositioningService: PractitionersPositioningService
) {

    @GetMapping
    fun info() = mapOf(
        "zones" to positions.keys.count(),
        "tags" to positions.values.count()
    )

    @GetMapping("tag/{tagId}")
    fun findTag(@PathVariable tagId: String) = positions[tagId] ?: "not found"

    @GetMapping("zone/{zoneId}")
    fun zone(@PathVariable zoneId: String) = positions.filter { (id, _) -> id == zoneId }.map { (_, tagId) -> tagId }

    @GetMapping("actual")
    fun actual() = practitionersPositioningService.actualUserIds()

    @GetMapping("list/office/{officeId}")
    fun practitioner(@PathVariable officeId: String) = practitionersPositioningService.listPractitionerIdsByOfficeId(officeId)

    @GetMapping("list/practitioner/{practitionerId}")
    fun office(@PathVariable practitionerId: String) = practitionersPositioningService.listOfficeIdsByPractitionerId(practitionerId)
}
