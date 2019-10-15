package ru.viscur.dh.integration.mis.rest.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@ComponentScan("ru.viscur.dh.integration.mis.rest")
class MisIntegrationRestConfig {
}