package ru.viscur.dh.apps.misintegrationtest.health

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/systems-services")
class HealthCheckController {

    /**
     * Стучимся сюда при деплое (ждем ответа перед тем как сказать что все ОК)
     */
    @GetMapping("/health-check")
    fun healthCheck() = "I'm online ;)"
}