package ru.viscur.dh.location.impl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.datastorage.impl.service.ReaderEventService

@RestController
@RequestMapping("/locations/log")
class ReaderEventLogController(
    private val readerEventService: ReaderEventService
) {

    @GetMapping
    fun list() = readerEventService.findAll()

}
