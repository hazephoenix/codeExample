package ru.viscur.dh.location.impl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.datastorage.impl.service.ZoneService

@RestController
@RequestMapping("/locations/zones")
class ZonesController(
    private val zoneService: ZoneService
) {

    @GetMapping
    fun list() = zoneService.findAll()

    @GetMapping("info/{zoneId}")
    fun info(@PathVariable zoneId: String) = zoneService.findById(zoneId)

}
