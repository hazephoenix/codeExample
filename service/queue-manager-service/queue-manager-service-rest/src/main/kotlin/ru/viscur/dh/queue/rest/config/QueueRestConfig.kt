package ru.viscur.dh.queue.rest.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@ComponentScan("ru.viscur.dh.queue.rest")
class QueueRestConfig {
}