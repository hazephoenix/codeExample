package ru.viscur.dh.apps.misintegrationtest.config

import org.springframework.context.annotation.*

@Configuration
@ComponentScan(
        basePackages = [
            "ru.viscur.dh.apps.misintegrationtest.service",
            "ru.viscur.dh.datastorage.impl",
            "ru.viscur.dh.integration.mis.api",
            "ru.viscur.dh.queue.impl.service"
        ]
)
class MisIntegrationTestConfig