package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mis-integration")
class MisIntegrationController {

    @GetMapping("/hello")
    fun hello() = "Hello world!"
}