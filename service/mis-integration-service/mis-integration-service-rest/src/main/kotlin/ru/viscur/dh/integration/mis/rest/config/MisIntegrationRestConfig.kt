package ru.viscur.dh.integration.mis.rest.config

import org.springframework.context.annotation.*
import org.springframework.web.servlet.config.annotation.*

@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = [
            "ru.viscur.dh.integration.mis.rest",
            "ru.viscur.dh.integration.mis.api",
            "ru.viscur.dh.queue.api"
        ])
class MisIntegrationRestConfig {
}