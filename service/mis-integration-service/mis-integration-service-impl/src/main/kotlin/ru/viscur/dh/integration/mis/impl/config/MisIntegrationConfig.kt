package ru.viscur.dh.integration.mis.impl.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

private const val BASE_PACKAGE = "ru.viscur.dh.integration.mis.impl"

/**
 * Автоконфигурация
 * (подробнее про механизм [тут](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html)
 */
@Configuration
@ComponentScan(
        basePackages = [
            BASE_PACKAGE
        ])
class MisIntegrationConfig