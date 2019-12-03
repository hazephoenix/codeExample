package ru.viscur.dh.location.impl

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms

@Configuration
@EnableJms
@ComponentScan(basePackages = ["ru.viscur.dh.location.impl"])
class RfidLocationServiceConfiguration
